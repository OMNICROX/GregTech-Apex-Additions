"""Connected textures for GTAA casings (LDLib CTM, same as GregTech's plascrete).

For every 16x16 casing texture with a border frame, writes <name>_ctm.png (2x2 sheet: no borders | side borders |
top/bottom borders | corner bits only) and <name>.png.mcmeta pointing LDLib at it. Textures that already have an
.mcmeta (animated / emissive) or no visible border are left alone.
"""
import json, os, sys
from PIL import Image

ROOT = sys.argv[1] if len(sys.argv) > 1 else 'src/main/resources/assets/gtapexadditions/textures/block/casing'
NS_PATH = 'gtapexadditions:block/casing/'
# only casings whose middle is plain enough to tile cleanly (checked by eye)
ONLY = ('qft_containment_casing', 'pur_corrosion_casing', 'pur_flocculation_casing', 'pur_industrial_casing',
        'pur_naquadah_casing', 'pur_naquadria_casing', 'pur_pressure_casing', 'nanochip_reinforcement_casing')
SKIP = ('overlay', 'emissive', '_ctm', 'top', 'bottom', 'glass', 'coil', 'core_', 'soil', 'chevron', 'gate_base', 'motor')

def lum(p): return 0.3 * p[0] + 0.59 * p[1] + 0.11 * p[2]

def border(im):
    """thickness of a frame that stands out from the inside (1-3 px), or 0"""
    px = im.load()
    def ring(k):  # pixels of ring k (0 = outermost)
        pts = [(x, k) for x in range(k, 16 - k)] + [(x, 15 - k) for x in range(k, 16 - k)]
        pts += [(k, y) for y in range(k + 1, 15 - k)] + [(15 - k, y) for y in range(k + 1, 15 - k)]
        return sum(lum(px[p]) for p in pts) / len(pts)
    inner = sum(lum(px[x, y]) for x in range(5, 11) for y in range(5, 11)) / 36
    rings = [ring(k) for k in range(4)]
    k = 0
    while k < 3 and abs(rings[k] - inner) > 10:
        k += 1
    return k

def make(im, k):
    src = im.load()
    def fill(keep_lr, keep_tb, keep_corners):
        out = im.copy(); o = out.load()
        for y in range(16):
            for x in range(16):
                in_l, in_r, in_t, in_b = x < k, x > 15 - k, y < k, y > 15 - k
                corner = (in_l or in_r) and (in_t or in_b)
                if corner and keep_corners: continue
                if (in_l or in_r) and keep_lr and not (in_t or in_b): continue
                if (in_t or in_b) and keep_tb and not (in_l or in_r): continue
                if (in_l or in_r) and keep_lr and corner and not keep_corners and keep_tb: continue
                if not (in_l or in_r or in_t or in_b): continue
                sx = x + k if in_l else x - k if in_r else x
                sy = y + k if in_t else y - k if in_b else y
                if (in_l or in_r) and keep_lr and corner: sy = y + k if in_t else y - k; sx = x
                if (in_t or in_b) and keep_tb and corner: sx = x + k if in_l else x - k; sy = y
                o[x, y] = src[sx, sy]
        return out
    sheet = Image.new('RGBA', (32, 32))
    sheet.paste(fill(False, False, False), (0, 0))   # connected all round
    sheet.paste(fill(True, False, False), (16, 0))   # left/right borders
    sheet.paste(fill(False, True, False), (0, 16))   # top/bottom borders
    sheet.paste(fill(False, False, True), (16, 16))  # corner bits
    return sheet

done = []
for f in sorted(os.listdir(ROOT)):
    if not f.endswith('.png') or any(s in f for s in SKIP) or f[:-4] not in ONLY: continue
    path = os.path.join(ROOT, f)
    if os.path.exists(path + '.mcmeta'): continue
    im = Image.open(path).convert('RGBA')
    if im.size != (16, 16): continue
    k = border(im)
    if k == 0 or k > 2: continue
    # a busy middle (a big emblem, a grid of parts) would repeat when connected: leave those alone
    px = im.load()
    vals = [lum(px[x, y]) for x in range(k, 16 - k) for y in range(k, 16 - k)]
    mean = sum(vals) / len(vals)
    if (sum((v - mean) ** 2 for v in vals) / len(vals)) ** 0.5 > 22: continue
    name = f[:-4]
    make(im, k).save(os.path.join(ROOT, name + '_ctm.png'))
    with open(path + '.mcmeta', 'w') as fh:
        json.dump({'ldlib': {'connection': NS_PATH + name + '_ctm'}}, fh, indent=2)
    done.append((name, k))
print(len(done), 'casings now connect:', ', '.join(f'{n}({k})' for n, k in done))
