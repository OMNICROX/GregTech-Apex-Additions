#!/usr/bin/env python3
"""GTNH-style Java String[][] structure literal -> GTAA structure JSON ([z][y], see mb2json.py).
usage: java2json.py <out.json> <key> <File.java> [index]   (index: which String[][] literal in the file, default 0)"""
import json, os, re, sys
from collections import Counter
out, key, src = sys.argv[1:4]
idx = int(sys.argv[4]) if len(sys.argv) > 4 else 0
text = open(src, encoding='utf-8').read()
starts = [m.end() for m in re.finditer(r'new String\s*\[\s*\]\s*\[\s*\]\s*\{', text)]
s = starts[idx]; depth = 1; i = s
while depth:
    c = text[i]
    if c == '"':
        i = text.index('"', i + 1)
    elif c == '{': depth += 1
    elif c == '}': depth -= 1
    i += 1
body = text[s:i - 1]
layers = [re.findall(r'"((?:[^"\\]|\\.)*)"', blk) for blk in re.findall(r'\{([^{}]*)\}', body)]
data = [[layers[y][z] for y in range(len(layers))] for z in range(len(layers[0]))]
d = json.load(open(out)) if os.path.exists(out) else {}
d[key] = data
json.dump(d, open(out, 'w'), separators=(',', ':'))
print(key, 'layers', len(data), 'rows', len(data[0]), 'width', len(data[0][0]),
      dict(Counter(c for l in layers for r in l for c in r)))
