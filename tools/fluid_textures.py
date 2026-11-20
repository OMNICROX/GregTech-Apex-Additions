#!/usr/bin/env python3
"""Animated fluid textures for GTAA's own fluids (replaces GT's plain tinted liquid).

python3 tools/fluid_textures.py src/main/resources/assets/gtapexadditions/textures/block/fluids
Each texture is a 16x16, 32-frame loop made of tiling domain-warped noise, coloured from the fluid's colour.
"""
import json
import math
import os
import sys

import numpy as np
from PIL import Image

OUT = sys.argv[1]
N, F = 16, 32
yy, xx = np.mgrid[0:N, 0:N].astype(float)


def field(seed, octaves, t, speed):
    """Tiling, time-looping noise in [0, 1]."""
    rng = np.random.default_rng(seed)
    v = np.zeros((N, N))
    total = 0
    for o in range(octaves):
        for _ in range(3):
            kx, ky = rng.integers(-2 - o, 3 + o, 2)
            if kx == 0 and ky == 0:
                kx = 1
            m = int(rng.integers(1, 3)) * (1 if rng.random() < 0.5 else -1)
            ph = rng.random() * 2 * math.pi
            a = 1.0 / (o + 1)
            v += a * np.sin(2 * math.pi * (kx * xx + ky * yy) / N + ph + speed * m * 2 * math.pi * t / F)
            total += a
    return (v / total + 1) / 2


def warped(seed, t, warp, speed, octaves=3):
    wx = field(seed + 1, 2, t, speed) - 0.5
    wy = field(seed + 2, 2, t, speed) - 0.5
    rng = np.random.default_rng(seed)
    v = np.zeros((N, N))
    total = 0
    for o in range(octaves):
        for _ in range(3):
            kx, ky = rng.integers(-2 - o, 3 + o, 2)
            if kx == 0 and ky == 0:
                ky = 1
            m = int(rng.integers(1, 3))
            ph = rng.random() * 2 * math.pi
            a = 1.0 / (o + 1)
            px = xx + warp * N * wx
            py = yy + warp * N * wy
            v += a * np.sin(2 * math.pi * (kx * px + ky * py) / N + ph + speed * m * 2 * math.pi * t / F)
            total += a
    return (v / total + 1) / 2


def hexrgb(c):
    return np.array([(c >> 16) & 255, (c >> 8) & 255, c & 255], float)


def ramp(v, stops):
    """stops: list of (pos, rgb)."""
    out = np.zeros(v.shape + (3,))
    for i in range(len(stops) - 1):
        p0, c0 = stops[i]
        p1, c1 = stops[i + 1]
        m = (v >= p0) & (v <= p1)
        f = ((v - p0) / max(1e-6, p1 - p0))[..., None]
        out[m] = (c0 + (c1 - c0) * f)[m]
    out[v < stops[0][0]] = stops[0][1]
    out[v > stops[-1][0]] = stops[-1][1]
    return out


def shade(c, k):
    return np.clip(c * k, 0, 255)


def mix(a, b, f):
    return a + (b - a) * f


STYLES = {
    # radioactive fuel: bright, bubbling, glowing specks
    'radioactive': dict(warp=0.35, speed=1, stops=lambda c: [(0, shade(c, .35)), (.45, c), (.8, mix(c, np.full(3, 255.), .45)), (1, np.full(3, 255.))], sparkle=0.93),
    # excited fuel: same but hotter and faster
    'excited': dict(warp=0.5, speed=2, stops=lambda c: [(0, shade(c, .5)), (.4, mix(c, np.array([255., 255, 160]), .25)), (.75, mix(c, np.full(3, 255.), .6)), (1, np.full(3, 255.))], sparkle=0.88),
    # naquadah fuels: dark, viscous, glowing veins
    'naquadah': dict(warp=0.55, speed=1, stops=lambda c: [(0, shade(c, .25)), (.55, shade(c, .7)), (.82, c), (.9, mix(c, np.array([160., 255, 120]), .7)), (1, shade(c, .8))], sparkle=0),
    # depleted / waste: murky, slow, low contrast
    'murky': dict(warp=0.25, speed=1, stops=lambda c: [(0, shade(c, .6)), (.5, c), (1, mix(c, np.full(3, 200.), .25))], sparkle=0),
    # oil-like: almost black with iridescent sheen
    'oily': dict(warp=0.6, speed=1, stops=lambda c: [(0, shade(c, .4)), (.6, c), (.86, mix(c, np.array([90., 60, 140]), .5)), (.93, mix(c, np.array([60., 140, 120]), .5)), (1, c)], sparkle=0),
    # clean chemical liquid: clear with soft caustics
    'clean': dict(warp=0.3, speed=1, stops=lambda c: [(0, shade(c, .75)), (.6, c), (.9, mix(c, np.full(3, 255.), .5)), (1, mix(c, np.full(3, 255.), .7))], sparkle=0.97),
}


def make(name, color, style, seed):
    st = STYLES[style]
    c = hexrgb(color)
    stops = st['stops'](c)
    frames = []
    rng = np.random.default_rng(seed + 99)
    specks = rng.random((N, N))
    for t in range(F):
        v = warped(seed, t, st['warp'], st['speed'])
        rgb = ramp(v, stops)
        if st['sparkle']:
            tw = (np.sin(2 * math.pi * (specks * 3 + t / F * 2)) + 1) / 2
            mask = (specks > st['sparkle']) & (tw > 0.6)
            rgb[mask] = mix(rgb[mask], np.full(3, 255.), 0.7)
        frames.append(np.clip(rgb, 0, 255).astype(np.uint8))
    img = np.concatenate(frames, axis=0)
    a = np.full(img.shape[:2] + (1,), 255, np.uint8)
    Image.fromarray(np.concatenate([img, a], axis=2), 'RGBA').save(f'{OUT}/fluid.{name}.png')
    json.dump({'animation': {'frametime': 2, 'interpolate': True}}, open(f'{OUT}/fluid.{name}.png.mcmeta', 'w'))


FLUIDS = [
    ('uranium_based_liquid_fuel', 0x2EE02E, 'radioactive'),
    ('uranium_based_liquid_fuel_excited', 0x7CFF5A, 'excited'),
    ('uranium_based_liquid_fuel_depleted', 0x6E8C6E, 'murky'),
    ('thorium_based_liquid_fuel', 0x7A1A60, 'radioactive'),
    ('thorium_based_liquid_fuel_excited', 0xC03AA0, 'excited'),
    ('thorium_based_liquid_fuel_depleted', 0x3C2836, 'murky'),
    ('plutonium_based_liquid_fuel', 0xEF1515, 'radioactive'),
    ('plutonium_based_liquid_fuel_excited', 0xFF6A3A, 'excited'),
    ('plutonium_based_liquid_fuel_depleted', 0x7A3232, 'murky'),
    ('naquadah_solution', 0x3D4A3D, 'naquadah'),
    ('light_naquadah_fuel', 0x92E05A, 'naquadah'),
    ('heavy_naquadah_fuel', 0x3A7A2B, 'naquadah'),
    ('naquadah_asphalt', 0x1E1E22, 'oily'),
    ('naquadah_based_liquid_fuel_mkv_depleted', 0x2E3A2E, 'murky'),
    ('naquadah_based_liquid_fuel_mkvi_depleted', 0x233023, 'murky'),
    ('polyaluminium_chloride', 0xD0D0B0, 'clean'),
    ('flocculation_waste', 0x6A6A40, 'murky'),
    ('space_time', 0x15123A, 'oily'),
]
mk = ['mki', 'mkii', 'mkiii', 'mkiv', 'mkv', 'mkvi']
mkc = [0x3E9100, 0x2B7A00, 0x1A6400, 0x0F4F00, 0x063B00, 0x002A00]
for i, (m, c) in enumerate(zip(mk, mkc)):
    FLUIDS.append((f'naquadah_based_liquid_fuel_{m}', c + 0x101010 * (6 - i) // 2, 'naquadah'))
    if i < 4:
        FLUIDS.append((f'naquadah_based_liquid_fuel_{m}_depleted', 0x333333 + 0x050505 * (i + 1), 'murky'))
gcol = [0x3E6FE0, 0x3A8CE0, 0x37A6E0, 0x34C0E0, 0x31D8E0, 0x2EE0C8, 0x2BE0A8, 0xE0E0FF]
for g, c in enumerate(gcol, 1):
    FLUIDS.append((f'grade_{g}_purified_water', c, 'clean'))

os.makedirs(OUT, exist_ok=True)
for i, (name, color, style) in enumerate(FLUIDS):
    make(name, color, style, 1000 + i * 17)
print(len(FLUIDS), 'fluid textures')
