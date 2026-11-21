#!/usr/bin/env python3
"""Builds GTAA's built-in "Apex Stargate" data pack.

  python3 tools/gen_apex_stargate_pack.py <Stargate Journey jar> <More Gates jar>

 - every Stargate Journey / More Gates recipe is disabled (forge:false); the only gate is the Ascension Gate's
 - gates (and their ring / chevron blocks) are cut out of every Stargate Journey structure; the structures stay
 - the Stargate Journey planets become Ad Astra destinations
"""
import io, json, os, shutil, sys, zipfile
import nbtlib

OUT = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'datapacks', 'apex_stargate')
GATE_BLOCKS = ('_stargate', '_ring', 'universe_stargate_chevron')
KEEP = ('goauld_transport_rings', 'ancient_transport_rings', 'goauld_ring_panel', 'stargate_shielding_ring')


def is_gate(name):
    if not name.startswith('sgjourney:'):
        return False
    path = name.split(':', 1)[1]
    if any(k in path for k in KEEP):
        return False
    return path.endswith(GATE_BLOCKS)


def main(sgj, more):
    shutil.rmtree(OUT, ignore_errors=True)
    os.makedirs(OUT)
    json.dump({'pack': {'pack_format': 15, 'description': 'Apex Ascension: Stargate changes (GTAA)'}},
              open(os.path.join(OUT, 'pack.mcmeta'), 'w'), indent=2)
    disabled = 0
    for jar in (sgj, more):
        with zipfile.ZipFile(jar) as z:
            for n in z.namelist():
                if n.startswith('data/') and '/recipes/' in n and n.endswith('.json'):
                    p = os.path.join(OUT, n)
                    os.makedirs(os.path.dirname(p), exist_ok=True)
                    json.dump({'conditions': [{'type': 'forge:false'}]}, open(p, 'w'))
                    disabled += 1
    edited = 0
    with zipfile.ZipFile(sgj) as z:
        for n in z.namelist():
            if not (n.startswith('data/') and n.endswith('.nbt')):
                continue
            raw = z.read(n)
            import gzip
            f = nbtlib.File.parse(io.BytesIO(gzip.decompress(raw)))
            palettes = [f['palette']] if 'palette' in f else list(f.get('palettes', []))
            gate_idx = set()
            for pal in palettes:
                for i, st in enumerate(pal):
                    if is_gate(str(st['Name'])):
                        gate_idx.add(i)
                        st['Name'] = nbtlib.String('minecraft:air')
                        if 'Properties' in st:
                            del st['Properties']
            if not gate_idx:
                continue
            for b in f['blocks']:
                if int(b['state']) in gate_idx and 'nbt' in b:
                    del b['nbt']
            p = os.path.join(OUT, n)
            os.makedirs(os.path.dirname(p), exist_ok=True)
            f.save(p, gzipped=True)
            edited += 1
    planets = {
        # Milky Way worlds of Stargate Journey (tier 5 rockets), Pegasus worlds (tier 6)
        'abydos': ('milky_way', 5, 45, 9.807), 'chulak': ('milky_way', 5, 22, 9.807),
        'cavum_tenebrae': ('milky_way', 5, -15, 11.2), 'rima': ('milky_way', 5, 28, 9.1),
        'tollan': ('milky_way', 5, 16, 9.807), 'unitas': ('milky_way', 5, 8, 8.6),
        'athos': ('pegasus', 6, 14, 9.807), 'lantea': ('pegasus', 6, 18, 9.807),
    }
    for name, (system, tier, temp, grav) in planets.items():
        p = os.path.join(OUT, 'data', 'gtapexadditions', 'planets', name + '.json')
        os.makedirs(os.path.dirname(p), exist_ok=True)
        json.dump({'dimension': 'sgjourney:' + name, 'gravity': grav, 'oxygen': True, 'solar_power': 16,
                   'solar_system': 'gtapexadditions:' + system, 'temperature': temp, 'tier': tier}, open(p, 'w'), indent=2)
    print(f'{disabled} recipes disabled, {edited} structures without gates, {len(planets)} planets')


if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])
