"""Minimal SNBT reader for FTB Quests files: enough to read back quest positions and dependencies the user set in game."""
import re

TOK = re.compile(r'\s*(?:(\{)|(\})|(\[)|(\])|("(?:\\.|[^"\\])*")|([^\s{}\[\]:"]+)|(:))', re.S)


def parse(text):
    toks = [m for m in TOK.finditer(text)]
    pos = 0
    def val():
        nonlocal pos
        m = toks[pos]; pos += 1
        if m.group(1):
            d = {}
            while not toks[pos].group(2):
                k = toks[pos]; pos += 1
                key = k.group(5)[1:-1] if k.group(5) else k.group(6)
                assert toks[pos].group(7), f'expected : after {key}'
                pos += 1
                d[key] = val()
            pos += 1
            return d
        if m.group(3):
            out = []
            # typed arrays like [I; 1, 2] are not used by FTBQ quest files we read
            while not toks[pos].group(4):
                out.append(val())
            pos += 1
            return out
        if m.group(5):
            return bytes(m.group(5)[1:-1], 'utf8').decode('unicode_escape')
        s = m.group(6)
        for suf, f in (('d', float), ('f', float), ('L', int), ('b', int)):
            if s.endswith(suf):
                try: return f(s[:-1])
                except ValueError: pass
        try: return int(s)
        except ValueError: pass
        try: return float(s)
        except ValueError: return {'true': True, 'false': False}.get(s, s)
    return val()


def layout(path):
    """quest id -> (x, y, size, shape, deps, hide_lines) as the user left them"""
    d = parse(open(path, encoding='utf8').read())
    out = {}
    for q in d.get('quests', []):
        out[q['id']] = dict(x=q.get('x', 0), y=q.get('y', 0), size=q.get('size'), shape=q.get('shape'),
                            deps=list(q.get('dependencies', [])), hide=q.get('hide_dependency_lines'))
    links = d.get('quest_links', [])
    return out, links
