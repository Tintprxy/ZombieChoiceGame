# Usage:
#   python validate_and_sanitize_json.py src/data/drive_story1.json
#   python validate_and_sanitize_json.py src/data/drive_story1.json --sanitize --out src/data/drive_story1.sanitized.json --mode replace
import json
import re
import sys
from pathlib import Path
import argparse
import unicodedata

# Allow underscore as a normal character (don't flag "_").
ID_RE = re.compile(r'^[A-Za-z0-9_]+$')
# Match any character that is NOT alphanumeric or underscore (e.g. @, #, punctuation, weird Unicode)
NON_ALNUM_RE = re.compile(r'[^A-Za-z0-9_]')

def find_ids(data):
    # data is expected to be a list of scene objects
    issues = []
    for i, scene in enumerate(data):
        sid = scene.get('id')
        if isinstance(sid, str) and not ID_RE.match(sid):
            issues.append(('scene.id', i, sid))
        # choices
        for j, choice in enumerate(scene.get('choices', [])):
            cid = choice.get('id')
            if isinstance(cid, str) and not ID_RE.match(cid):
                issues.append(('choice.id', f'{i}.{j}', cid))
    return issues

def sanitize_id(s, mode='underscore'):
    if mode == 'underscore':
        return NON_ALNUM_RE.sub('_', s)
    else:
        return NON_ALNUM_RE.sub('', s)

def apply_sanitization(data, mode='underscore'):
    # build mapping old -> new for scene ids & choice ids
    mapping = {}
    for scene in data:
        sid = scene.get('id')
        if isinstance(sid, str):
            new = sanitize_id(sid, mode)
            if new != sid:
                mapping[sid] = new
    # also sanitize choice target ids
    for scene in data:
        for choice in scene.get('choices', []):
            cid = choice.get('id')
            if isinstance(cid, str):
                new = sanitize_id(cid, mode)
                if new != cid:
                    mapping[cid] = new
    # Apply mapping to scene ids and choice ids (only exact matches)
    for scene in data:
        sid = scene.get('id')
        if sid in mapping:
            scene['id'] = mapping[sid]
        for choice in scene.get('choices', []):
            cid = choice.get('id')
            if cid in mapping:
                choice['id'] = mapping[cid]
    return mapping

def describe_chars(s):
    chars = NON_ALNUM_RE.findall(s)
    described = []
    for c in chars:
        name = unicodedata.name(c, 'UNNAMED')
        described.append(f"'{c}' U+{ord(c):04X} ({name})")
    return described

def main():
    p = argparse.ArgumentParser(description='Validate and optionally sanitize scene IDs in story JSON.')
    p.add_argument('file', help='path to story json (list of scenes)')
    p.add_argument('--sanitize', action='store_true', help='write a sanitized copy')
    p.add_argument('--out', default=None, help='output path when --sanitize (defaults to <input>.sanitized.json)')
    p.add_argument('--mode', choices=['underscore','remove'], default='underscore', help='how to replace non-alnum: underscore or remove')
    p.add_argument('--details', action='store_true', help='show offending characters with Unicode names/codepoints')
    args = p.parse_args()

    path = Path(args.file)
    if not path.exists():
        print('File not found:', path)
        sys.exit(2)

    text = path.read_text(encoding='utf-8')
    try:
        data = json.loads(text)
    except Exception as ex:
        print('Failed to parse JSON:', ex)
        sys.exit(2)

    issues = find_ids(data)
    if not issues:
        print('No non-alphanumeric IDs found.')
    else:
        print('Found non-alphanumeric IDs:')
        for kind, loc, val in issues:
            chars = NON_ALNUM_RE.findall(val)
            if args.details and chars:
                descr = describe_chars(val)
                print(f'  {kind} @ {loc}: "{val}" -> {descr}')
            else:
                print(f'  {kind} @ {loc}: "{val}" (non-alnum chars: {chars})')

    if args.sanitize:
        out_path = Path(args.out) if args.out else path.with_suffix('.sanitized.json')
        mapping = apply_sanitization(data, args.mode)
        if mapping:
            print('\nApplied sanitization mapping (old -> new):')
            for k,v in mapping.items():
                print(f'  {k} -> {v}')
        else:
            print('\nNo ids required sanitization.')
        out_path.write_text(json.dumps(data, indent=2, ensure_ascii=False), encoding='utf-8')
        print('Wrote sanitized file to', out_path)

if __name__ == '__main__':
    main()