"""FTB Quests SNBT writer and the Quest / Chapter model for the Apex Ascension quest book."""
import hashlib, json, re, os

S = '/tmp/claude-0/-home-claude/e2e2a1ea-2802-5c5f-8206-9605d72f9e6a/scratchpad/'
NAMES = dict(json.load(open(S + 'apex_quest_items.json'))['items'])
TIERS = json.load(open(S + 'tiers.json'))['tier']
TN = ['ULV', 'LV', 'MV', 'HV', 'EV', 'IV', 'LuV', 'ZPM', 'UV', 'UHV', 'UEV', 'UIV', 'UXV', 'OpV', 'MAX']
TP = ['ulv', 'lv', 'mv', 'hv', 'ev', 'iv', 'luv', 'zpm', 'uv', 'uhv', 'uev', 'uiv', 'uxv', 'opv', 'max']
TC = ['&8', '&7', '&b', '&6', '&5', '&9', '&d', '&c', '&3', '&4', '&a', '&2', '&e', '&9&l', '&c&l']   # tier colours
MISSING = set()


def hid(key):
    # FTB Quests reads ids as signed hex longs; anything above 7FFF... gets a new random id and loses its links
    return format(int(hashlib.sha1(('apex_quests:' + key).encode()).hexdigest()[:16], 16) & 0x7FFFFFFFFFFFFFFF, '016X')


def table_hex(name):
    return format(int(hid('table/' + name), 16) & 0x7FFFFFFFFFFFFFFF, '016X')


def exists(item):
    return item in NAMES


def name(item):
    n = NAMES.get(item, item.split(':')[-1].replace('_', ' ').title())
    return re.sub(r'§.', '', n).strip()


def tier(item):
    return TIERS.get(item, 99)


class Long:
    def __init__(s, v): s.v = int(v)


class Dbl:
    def __init__(s, v): s.v = float(v)


def snbt_str(s):
    return '"' + s.replace('\\', '\\\\').replace('"', '\\"') + '"'


def to_snbt(v, ind=0):
    p = '\t' * ind
    if isinstance(v, bool): return 'true' if v else 'false'
    if isinstance(v, Long): return f'{v.v}L'
    if isinstance(v, Dbl): return f'{v.v}d'
    if type(v).__name__ == 'Byte': return f'{int(v)}b'
    if type(v).__name__ == 'Flt': return f'{float(v)}f'
    if isinstance(v, int): return str(v)
    if isinstance(v, float): return f'{v}d'
    if isinstance(v, str): return snbt_str(v)
    if isinstance(v, list):
        if not v: return '[ ]'
        if all(isinstance(x, str) for x in v):
            return '[\n' + ''.join(f'{p}\t{snbt_str(x)}\n' for x in v) + f'{p}]'
        return '[\n' + ''.join(f'{p}\t{to_snbt(x, ind + 1)}\n' for x in v) + f'{p}]'
    if isinstance(v, dict):
        if not v: return '{ }'
        return '{\n' + ''.join(f'{p}\t{k}: {to_snbt(v[k], ind + 1)}\n' for k in sorted(v)) + p + '}'
    raise TypeError(type(v))


class Q:
    """One quest. tasks: list of 'item' / 'item*N' / ['a','b'] (any of) / 'check'. desc: str or list of lines."""

    def __init__(self, key, title, tasks, desc='', deps=(), reward=None, xp=None, icon=None, shape=None, size=None,
                 optional=False, hide_lines=False, subtitle=''):
        self.key, self.title, self.tasks, self.desc = key, title, tasks, desc
        self.deps = list(deps)
        self.reward, self.xp, self.icon, self.shape, self.size = reward, xp, icon, shape, size
        self.optional, self.hide_lines, self.subtitle = optional, hide_lines, subtitle
        self.x = self.y = 0.0

    @property
    def id(self):
        return hid(self.key)

    def valid_tasks(self):
        out = []
        for t in self.tasks:
            if t == 'check' or (isinstance(t, str) and t.split(':')[0] in ('dim', 'kill', 'structure')):
                out.append(t); continue
            if isinstance(t, list):
                alts = [a for a in t if exists(a.split('*')[0])]
                if alts: out.append(alts)
                else: MISSING.update(t)
                continue
            if exists(t.split('*')[0]): out.append(t)
            else: MISSING.add(t)
        return out


class Ch:
    def __init__(self, key, title, icon, group, quests, subtitle='', order=0):
        self.key, self.title, self.icon, self.group = key, title, icon, group
        self.quests, self.subtitle, self.order = quests, subtitle, order


def task_snbt(qid, i, t):
    tid = hid(f'{qid}/task/{i}')
    if t == 'check':
        return {'id': tid, 'type': 'checkmark'}
    if t.startswith('dim:'):
        return {'id': tid, 'type': 'dimension', 'dimension': t[4:]}
    if t.startswith('structure:'):
        return {'id': tid, 'type': 'structure', 'structure': t[10:]}
    if t.startswith('kill:'):
        ent, n = (t[5:].split('*') + ['1'])[:2]
        return {'id': tid, 'type': 'kill', 'entity': ent, 'value': Long(int(n))}
    if isinstance(t, list):
        # any of several items: an item filter task would need FTB Filter System; use the first and say so
        t = t[0]
    item, n = (t.split('*') + ['1'])[:2]
    d = {'id': tid, 'type': 'item', 'item': item}
    if int(n) > 1: d['count'] = Long(int(n))
    return d


def desc_lines(desc):
    if isinstance(desc, str):
        desc = [desc] if desc else []
    out = []
    for i, para in enumerate(desc):
        if i: out.append('')
        out.append(para)
    return out


def quest_snbt(q, ids_present):
    tasks = q.valid_tasks()
    d = {'id': q.id, 'x': Dbl(q.x), 'y': Dbl(q.y), 'title': q.title}
    lines = desc_lines(q.desc)
    if lines: d['description'] = lines
    if q.subtitle: d['subtitle'] = q.subtitle
    deps = [hid(k) for k in q.deps if hid(k) in ids_present]
    if deps: d['dependencies'] = deps
    d['tasks'] = [task_snbt(q.id, i, t) for i, t in enumerate(tasks)] or [{'id': hid(q.id + '/task/c'), 'type': 'checkmark'}]
    rewards = []
    if q.xp: rewards.append({'id': hid(q.id + '/xp'), 'type': 'xp', 'xp': q.xp})
    if q.reward: rewards.append({'id': hid(q.id + '/bag'), 'type': 'random', 'table_id': Long(int(table_hex(q.reward), 16))})
    if rewards: d['rewards'] = rewards
    if q.icon: d['icon'] = q.icon
    if q.shape: d['shape'] = q.shape
    if q.size: d['size'] = Dbl(q.size)
    if q.optional: d['optional'] = True
    if q.hide_lines: d['hide_dependency_lines'] = True
    d['min_width'] = 260
    return d
