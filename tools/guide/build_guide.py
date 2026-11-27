"""Writes the Apex Guide (GuideME markdown) into src/main/resources/assets/gtapexadditions/guides/gtapexadditions/guide/."""
import os, sys, shutil
sys.path.insert(0, os.path.dirname(__file__))
from pages_core import P
import pages_prog, pages_world, pages_multis, pages_lines  # noqa: F401 (register pages)

ROOT = os.path.join(os.path.dirname(__file__), '../../src/main/resources/assets/gtapexadditions/guides/gtapexadditions/guide')
ROOT = os.path.normpath(ROOT)

def fm(p):
    lines = ['---', 'navigation:', f'  title: "{p["title"]}"']
    if p['parent']: lines.append(f'  parent: {p["parent"]}')
    lines.append(f'  position: {p["pos"]}')
    if p['icon']: lines.append(f'  icon: {p["icon"]}')
    if p['cats']:
        lines.append('categories:'); lines += [f'- {c}' for c in p['cats']]
    if p['items']:
        lines.append('item_ids:'); lines += [f'- {i}' for i in p['items']]
    lines.append('---')
    return '\n'.join(lines) + '\n\n'

if __name__ == '__main__':
    keep_assets = os.path.join(ROOT, 'assets')
    for d in os.listdir(ROOT) if os.path.isdir(ROOT) else []:
        if d != 'assets':
            pth = os.path.join(ROOT, d)
            shutil.rmtree(pth) if os.path.isdir(pth) else os.remove(pth)
    for path, p in P.items():
        out = os.path.join(ROOT, path)
        os.makedirs(os.path.dirname(out), exist_ok=True)
        open(out, 'w').write(fm(p) + p['body'])
    print(len(P), 'pages')
