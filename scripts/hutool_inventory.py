#!/usr/bin/env python3
"""Build the Hutool API inventory that the coverage report is counted from.

The inventory is derived from the upstream Java source tree, not from memory: every Java file is
one item, so the totals in `development/coverage.md` can be re-checked by re-running this script.

Usage:
    python scripts/hutool_inventory.py --hutool <path to a hutool clone> --out build/hutool-inventory.json

Status of an item is decided in `hutool_coverage.py`; this script only collects facts.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path

# Modules whose classes are all part of the evaluated scope. Everything else is excluded per module
# (see EXCLUDED_MODULES in hutool_coverage.py) because the module itself cannot exist in commonMain.
CLASS_SCOPE_MODULES = (
    "hutool-core",
    "hutool-json",
    "hutool-setting",
    "hutool-cache",
    "hutool-bloomFilter",
    "hutool-cron",
    "hutool-dfa",
    "hutool-log",
    "hutool-crypto",
    "hutool-jwt",
)

PACKAGE_RE = re.compile(r"^package\s+([\w.]+)", re.MULTILINE)
TYPE_RE = re.compile(r"^(?:public\s+)?(?:final\s+|abstract\s+)?(?:class|interface|enum|@interface)\s+(\w+)", re.MULTILINE)
PUBLIC_MEMBER_RE = re.compile(
    r"^\s*(?:public|protected)\s+(?:static\s+)?(?:final\s+)?"
    r"(?:<[^>]+>\s+)?"
    r"([\w.$<>\[\], ?]+?)\s+(\w+)\s*\(",
    re.MULTILINE,
)


def collect(hutool_root: Path) -> list[dict]:
    items: list[dict] = []
    for module_dir in sorted(hutool_root.glob("hutool-*")):
        java_root = module_dir / "src" / "main" / "java"
        if not java_root.is_dir():
            continue
        for java_file in sorted(java_root.rglob("*.java")):
            text = java_file.read_text(encoding="utf-8", errors="replace")
            package = (PACKAGE_RE.search(text) or [None, ""])[1]
            relative = java_file.relative_to(java_root).as_posix()
            fqcn = relative[: -len(".java")].replace("/", ".")
            match = TYPE_RE.search(text)
            items.append(
                {
                    "module": module_dir.name,
                    "fqcn": fqcn,
                    "package": package,
                    "name": match.group(1) if match else java_file.stem,
                    "path": str(java_file.relative_to(hutool_root).as_posix()),
                    "publicMembers": len(PUBLIC_MEMBER_RE.findall(text)),
                    "inClassScope": module_dir.name in CLASS_SCOPE_MODULES,
                }
            )
    return items


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--hutool", default=r"S:\workspace\github\chinabugotech\hutool")
    parser.add_argument("--out", default="build/hutool-inventory.json")
    args = parser.parse_args()

    root = Path(args.hutool)
    if not (root / "pom.xml").is_file():
        print(f"not a hutool clone: {root}", file=sys.stderr)
        return 1

    items = collect(root)
    out = Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(items, ensure_ascii=False, indent=2), encoding="utf-8")

    by_module: dict[str, int] = {}
    members_by_module: dict[str, int] = {}
    for item in items:
        by_module[item["module"]] = by_module.get(item["module"], 0) + 1
        members_by_module[item["module"]] = members_by_module.get(item["module"], 0) + item["publicMembers"]
    print(f"{len(items)} items from {root}")
    for module, count in sorted(by_module.items()):
        scope = "scope" if module in CLASS_SCOPE_MODULES else "module-excluded"
        print(f"  {module:<24} {count:>5} classes {members_by_module[module]:>7} public members  ({scope})")
    print(f"written to {out}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
