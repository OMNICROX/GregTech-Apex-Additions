"""Moves quests out of a broad chapter into a page of their own (one page per mod, like GT New Horizons).
The quest keeps everything but its key; build.py renames the requirements that pointed at the old key."""
import importlib
from fw import Chapter

RENAMES = {}     # old quest key -> new quest key
ROOTS = {}       # (old root key, new chapter key) -> new root key


def carve(src_module, chapter, keys, start):
    """chapter: the new Chapter. keys: quest keys (without prefix) to move from src_module, in page order.
    start: the new page's first quest (already added to `chapter`); moved quests that hung on the old page's first quest hang on it."""
    src = importlib.import_module(src_module).C
    old_root = src.quests[0].key
    by = {q.key.split('/', 1)[1]: q for q in src.quests}
    for k in keys:
        q = by[k]
        src.quests.remove(q)
        old = q.key
        q.key = f'{chapter.key}/{k}'
        RENAMES[old] = q.key
        q.deps = [start.key if d == old_root else d for d in q.deps]
        q.deps0 = list(q.deps)
        chapter.quests.append(q)
    return chapter


def fix(chapters):
    for ch in chapters:
        for q in ch.quests:
            q.deps = list(dict.fromkeys(RENAMES.get(d, d) for d in q.deps))
            q.deps0 = list(q.deps)
