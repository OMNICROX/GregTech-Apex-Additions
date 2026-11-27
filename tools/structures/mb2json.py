#!/usr/bin/env python3
"""GT-Not-Leisure .mb structure (one line per layer top to bottom, rows front to back, comma separated)
-> GTAA structure JSON ([layer front-to-back][row top-to-bottom], the GTNH shapeMain layout ApexStructures reads).

usage: mb2json.py <out.json> <key>=<file.mb> [<key>=<file.mb> ...]   (adds to / replaces keys in out.json)
"""
import json, os, sys

def convert(path):
    lines = open(path, encoding='utf-8').read().rstrip('\n').split('\n')
    ys = [l.split(',') for l in lines]          # [y][z] -> chars along x
    depth = len(ys[0])
    return [[ys[y][z] for y in range(len(ys))] for z in range(depth)]   # [z][y]

out = sys.argv[1]
data = json.load(open(out)) if os.path.exists(out) else {}
for arg in sys.argv[2:]:
    key, path = arg.split('=', 1)
    data[key] = convert(path)
    print(key, 'layers', len(data[key]), 'rows', len(data[key][0]), 'width', len(data[key][0][0]))
json.dump(data, open(out, 'w'), separators=(',', ':'))
