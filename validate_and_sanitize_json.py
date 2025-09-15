# Usage:
#   python validate_and_sanitize_json.py src/data/drive_story1.json
#   python validate_and_sanitize_json.py src/data/drive_story1.json --sanitize --out src/data/drive_story1.sanitized.json --mode replace
#   python validate_and_sanitize_json.py src/data/drive_story1.json --sanitize --sanitize-text --out src/data/drive_story1.sanitized.json
import json
import re
import sys
from pathlib import Path
import argparse
import unicodedata

ID_RE = re.compile(r'^[A-Za-z0-9_]+$')
NON_ALNUM_RE = re.compile(r'[^A-Za-z0-9_]')

def find_ids(data):
    issues = []
    for i, scene in enumerate(data):
        sid = scene.get('id')
        if isinstance(sid, str) and not ID_RE.match(sid):
            issues.append(('scene.id', i, sid))
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
    mapping = {}
    for scene in data:
        sid = scene.get('id')
        if isinstance(sid, str):
            new = sanitize_id(sid, mode)
            if new != sid:
                mapping[sid] = new
    for scene in data:
        for choice in scene.get('choices', []):
            cid = choice.get('id')
            if isinstance(cid, str):
                new = sanitize_id(cid, mode)
                if new != cid:
                    mapping[cid] = new
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

REPLACEMENTS = {
    '\u2010': '-',  # hyphen
    '\u2011': '-',  # non-breaking hyphen
    '\u2012': '-',  # figure dash
    '\u2013': '-',  # en dash –
    '\u2014': '-',  # em dash —
    '\u2015': '-',  # horizontal bar
    '\u2212': '-',  # minus sign −
    '\u2018': "'",  # left single quote ‘
    '\u2019': "'",  # right single quote ’
    '\u201B': "'",  # single high-reversed-9 quote
    '\u2032': "'",  # prime ′
    '\u201C': '"',  # left double quote “
    '\u201D': '"',  # right double quote ”
    '\u201F': '"',  # double high-reversed-9 quote
    '\u2033': '"',  # double prime ″
    '\u2026': '...',# ellipsis …
    '\u00A0': ' ',  # no-break space
    '\u2000': ' ',  # en quad
    '\u2001': ' ',  # em quad
    '\u2002': ' ',  # en space
    '\u2003': ' ',  # em space
    '\u2004': ' ',  # three-per-em space
    '\u2005': ' ',  # four-per-em space
    '\u2006': ' ',  # six-per-em space
    '\u2007': ' ',  # figure space
    '\u2008': ' ',  # punctuation space
    '\u2009': ' ',  # thin space
    '\u200A': ' ',  # hair space
    '\u202F': ' ',  # narrow no-break space
    '\u205F': ' ',  # medium mathematical space
    '\u3000': ' ',  # ideographic space
    '\u00AD': '',   # soft hyphen
    '\u200B': '',   # zero-width space
    '\u200C': '',   # zero-width non-joiner
    '\u200D': '',   # zero-width joiner
    '\u2060': '',   # word joiner
    '\uFEFF': '',   # zero-width no-break space (BOM)
}

def sanitize_ascii(s: str) -> str:
    if not isinstance(s, str):
        return s
    s = unicodedata.normalize('NFC', s)
    for k, v in REPLACEMENTS.items():
        if k in s:
            s = s.replace(k, v)
    return s

def apply_text_sanitization(data):
    for scene in data:
        if 'prompt' in scene and isinstance(scene['prompt'], str):
            scene['prompt'] = sanitize_ascii(scene['prompt'])
        if 'choices' in scene and isinstance(scene['choices'], list):
            for choice in scene['choices']:
                if 'label' in choice and isinstance(choice['label'], str):
                    choice['label'] = sanitize_ascii(choice['label'])

def main():
    p = argparse.ArgumentParser(description='Validate and optionally sanitize scene IDs in story JSON.')
    p.add_argument('file', help='path to story json (list of scenes)')
    p.add_argument('--sanitize', action='store_true', help='write a sanitized copy (IDs; see --mode)')
    p.add_argument('--sanitize-text', action='store_true', help='clean non-ASCII/fancy punctuation from prompts and labels')
    p.add_argument('--out', default=None, help='output path when --sanitize (defaults to <input>.sanitized.json)')
    p.add_argument('--mode', choices=['underscore','remove'], default='underscore',
                   help='how to replace non-alnum in IDs: underscore or remove')
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

    if args.sanitize or args.sanitize_text:
        out_path = Path(args.out) if args.out else path.with_suffix('.sanitized.json')

        mapping = {}
        if args.sanitize:
            mapping = apply_sanitization(data, args.mode)

        if args.sanitize_text:
            apply_text_sanitization(data)

        if mapping:
            print('\nApplied sanitization mapping (old -> new):')
            for k, v in mapping.items():
                print(f'  {k} -> {v}')
        else:
            if args.sanitize:
                print('\nNo ids required sanitization.')

        out_path.write_text(json.dumps(data, indent=2, ensure_ascii=False), encoding='utf-8')
        print('Wrote sanitized file to', out_path)

if __name__ == '__main__':
    main()
