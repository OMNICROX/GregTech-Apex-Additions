"""A small SNBT reader for FTB Quests files (enough for what FTB Quests writes)."""
import re

from core import Long, Dbl

_num = re.compile(r'-?\d+(\.\d+)?([eE][-+]?\d+)?[bBsSlLfFdD]?$')


class Byte(int):
    pass


def parse(text):
    p = _P(text)
    v = p.value()
    return v


class _P:
    def __init__(s, t):
        s.t, s.i = t, 0

    def ws(s):
        t = s.t
        while s.i < len(t):
            c = t[s.i]
            if c in ' \t\r\n,':
                s.i += 1
            elif c == '/' and t.startswith('//', s.i):
                s.i = t.index('\n', s.i) if '\n' in t[s.i:] else len(t)
            else:
                break

    def value(s):
        s.ws()
        c = s.t[s.i]
        if c == '{': return s.obj()
        if c == '[': return s.arr()
        if c == '"' or c == "'": return s.string()
        return s.bare()

    def obj(s):
        s.i += 1
        d = {}
        while True:
            s.ws()
            if s.t[s.i] == '}':
                s.i += 1
                return d
            k = s.string() if s.t[s.i] in '"\'' else s.word()
            s.ws()
            assert s.t[s.i] == ':', (s.t[s.i - 30:s.i + 30])
            s.i += 1
            d[k] = s.value()

    def arr(s):
        s.i += 1
        s.ws()
        if re.match(r'[BIL];', s.t[s.i:s.i + 2]):
            s.i += 2
        out = []
        while True:
            s.ws()
            if s.t[s.i] == ']':
                s.i += 1
                return out
            out.append(s.value())

    def string(s):
        q = s.t[s.i]
        s.i += 1
        b = []
        while True:
            c = s.t[s.i]
            if c == '\\':
                b.append(s.t[s.i + 1])
                s.i += 2
                continue
            s.i += 1
            if c == q: return ''.join(b)
            b.append(c)

    def word(s):
        m = re.compile(r'[A-Za-z0-9_.+\-]+').match(s.t, s.i)
        s.i = m.end()
        return m.group()

    def bare(s):
        w = s.word()
        if w == 'true': return True
        if w == 'false': return False
        if _num.match(w):
            suf = w[-1]
            if suf in 'lL': return Long(int(w[:-1]))
            if suf in 'dD': return Dbl(float(w[:-1]))
            if suf in 'fF': return Flt(float(w[:-1]))
            if suf in 'bB': return Byte(int(w[:-1]))
            if suf in 'sS': return int(w[:-1])
            return float(w) if ('.' in w or 'e' in w.lower()) else int(w)
        return w


class Flt(float):
    pass
