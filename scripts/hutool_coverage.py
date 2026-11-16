#!/usr/bin/env python3
"""Recompute the porting coverage of kmp-hutool against the upstream Hutool sources.

Each upstream Java class is one item. An item is

* **done**      when some Kotlin file in `*/src/commonMain/kotlin` carries a KDoc tag
                `@hutool <fully qualified class name>`;
* **excluded**  when `scripts/hutool-exclusions.json` names it, or when the upstream source
                references a JDK API that Kotlin common code cannot use (reflection, java.io,
                AWT, JDBC, threads, javax.tools, ...). The reason recorded is the API found, so
                every exclusion can be checked against the Java file;
* **pending**   otherwise.

Usage:
    python scripts/hutool_coverage.py --hutool <hutool clone> --out docs/development/coverage.md
    python scripts/hutool_coverage.py --check   # only print the totals

The Markdown report is the deliverable; `build/hutool-coverage.json` holds the same data for
machine checks.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import Counter, defaultdict
from pathlib import Path

import hutool_inventory

ROOT = Path(__file__).resolve().parent.parent
TAG_RE = re.compile(r"@hutool\s+([A-Za-z_][\w.$]*)")
MODULE_EXCLUDED_REASONS_DEFAULT = (
    "The module cannot exist in Kotlin common code (see scripts/hutool-exclusions.json)."
)

# (regular expression matched against the Java source, human readable reason). The first match
# decides the reason, so the list is ordered from "most obviously impossible" to "partly portable".
HUSK_RULES: tuple[tuple[str, str], ...] = (
    (r"java\.awt|javax\.swing|java\.awt\.image", "AWT/Swing graphics"),
    (r"javax\.tools", "javax.tools compiler API"),
    (r"java\.sql|javax\.sql|DataSource", "JDBC"),
    (r"java\.lang\.reflect|getDeclared|Class\.forName|getClass\(\)\.get", "runtime reflection"),
    (r"java\.nio|java\.io\.|java\.util\.zip|GZIP", "java.io / java.nio streams and files"),
    (r"java\.util\.concurrent|Thread|Runnable|synchronized|Executor", "threads and concurrency"),
    (r"java\.net|Socket|ServerSocket|Datagram", "java.net sockets"),
    (r"java\.security|javax\.crypto|MessageDigest|Cipher|Signature|KeyStore", "java.security providers"),
    (r"java\.math\.BigDecimal|BigDecimal|BigInteger", "BigDecimal/BigInteger"),
    (r"java\.util\.Date|Calendar|SimpleDateFormat|LocalDateTime|ZonedDateTime", "java.util.Date and java.time"),
    (r"ProcessBuilder|Runtime\.getRuntime", "process execution"),
    (r"java\.util\.stream|Collectors", "java.util.stream"),
    (r"java\.text\.Normalizer|Normalizer", "java.text.Normalizer"),
    (r"Charset\.forName|new String\(\w+, ", "JVM charset handling"),
)


def load_exclusions(path: Path) -> dict:
    if not path.is_file():
        return {"modules": {}, "classes": {}}
    return json.loads(path.read_text(encoding="utf-8"))


def scan_kotlin_sources(source_roots: list[Path]) -> tuple[dict[str, list[str]], int]:
    """Map `@hutool <fqcn>` tags to the Kotlin files that declare them."""
    tags: dict[str, list[str]] = defaultdict(list)
    files = 0
    for root in source_roots:
        for kt in sorted(root.rglob("*.kt")):
            files += 1
            text = kt.read_text(encoding="utf-8", errors="replace")
            for match in TAG_RE.finditer(text):
                try:
                    display = kt.resolve().relative_to(ROOT).as_posix()
                except ValueError:
                    display = kt.as_posix()
                tags[match.group(1)].append(display)
    return dict(tags), files


COMMENT_RE = re.compile(r"/*.*?\*/|//[^\n]*", re.S)


def strip_comments(java_source: str) -> str:
    """Drop block and line comments so a class is judged by its code, not its Javadoc prose."""
    return COMMENT_RE.sub("", java_source)


def husk_reason(java_source: str) -> str | None:
    code = strip_comments(java_source)
    for pattern, reason in HUSK_RULES:
        if re.search(pattern, code):
            return (
                f"Upstream source uses {reason}, which neither Kotlin common code nor a "
                f"multiplatform replacement provides."
            )
    return None


def classify(items: list[dict], done_tags: dict[str, list[str]], exclusions: dict) -> list[dict]:
    module_exclusions: dict[str, str] = exclusions.get("modules", {})
    class_exclusions: dict[str, str] = exclusions.get("classes", {})
    hutool_root = Path(exclusions["_root"]) if "_root" in exclusions else None

    result: list[dict] = []
    for item in items:
        record = dict(item)
        files = done_tags.get(item["fqcn"])
        if files:
            record["status"] = "done"
            record["reason"] = ""
            record["kotlin"] = files
        elif item["fqcn"] in class_exclusions:
            record["status"] = "excluded"
            record["reason"] = class_exclusions[item["fqcn"]]
        elif not item["inClassScope"]:
            record["status"] = "excluded"
            record["reason"] = module_exclusions.get(
                item["module"],
                f"Whole module out of scope: {MODULE_EXCLUDED_REASONS_DEFAULT}",
            )
        else:
            reason = None
            if hutool_root is not None:
                java_file = hutool_root / item["path"]
                if java_file.is_file():
                    reason = husk_reason(java_file.read_text(encoding="utf-8", errors="replace"))
            record["status"] = "excluded" if reason else "pending"
            record["reason"] = reason or ""
            record["kotlin"] = []
        result.append(record)
    return result


def markdown(records: list[dict], meta: dict) -> str:
    by_module: dict[str, list[dict]] = defaultdict(list)
    for record in records:
        by_module[record["module"]].append(record)

    lines: list[str] = []
    lines.append("# Hutool port coverage / Hutool 移植覆盖率")
    lines.append("")
    lines.append("Generated by `scripts/hutool_coverage.py`; do not edit by hand.")
    lines.append("")
    lines.append("## Source and scope / 来源与范围")
    lines.append("")
    lines.append(f"- Upstream: `{meta['hutool']}` (Hutool {meta['version']}), checked {meta['date']}.")
    lines.append("- Granularity: one upstream Java class (`*.java`) is one item.")
    lines.append(
        "- Class-level scope: "
        + ", ".join(f"`{module}`" for module in meta["scopeModules"])
        + "."
    )
    lines.append(
        "- Other Hutool modules are excluded per module: their whole runtime model (servlet, "
        "JDBC drivers, POI, socket servers, GraalJS) has no Kotlin common counterpart."
    )
    lines.append(
        "- An item counts as *done* when a Kotlin file in `*/src/commonMain/kotlin` is tagged "
        "`@hutool <fqcn>`; `scripts/hutool_coverage.py` recomputes every number below."
    )
    lines.append("")
    lines.append("## Totals / 汇总")
    lines.append("")
    lines.append("| Module | Total | Done | Excluded | Pending |")
    lines.append("|---|---:|---:|---:|---:|")
    for module, module_records in sorted(by_module.items()):
        counts = Counter(record["status"] for record in module_records)
        lines.append(
            f"| `{module}` | {len(module_records)} | {counts['done']} | "
            f"{counts['excluded']} | {counts['pending']} |"
        )
    counts = Counter(record["status"] for record in records)
    lines.append(
        f"| **total** | **{len(records)}** | **{counts['done']}** | "
        f"**{counts['excluded']}** | **{counts['pending']}** |"
    )
    lines.append("")
    lines.append(
        f"Coverage of the class-level scope: **{counts['done']} / "
        f"{len(records) - counts['excluded']}** "
        f"({counts['done'] / max(1, len(records) - counts['excluded']) * 100:.1f}%) "
        "of the in-scope classes are ported; classes excluded with a reason are not counted "
        "in that denominator."
    )
    lines.append("")
    lines.append("## Excluded / 明确不做")
    lines.append("")
    lines.append("### Modules")
    lines.append("")
    module_reason: dict[str, str] = {}
    for record in records:
        if not record["inClassScope"]:
            module_reason[record["module"]] = record["reason"]
    lines.append("| Module | Classes | Reason |")
    lines.append("|---|---:|---|")
    module_totals = Counter(record["module"] for record in records if not record["inClassScope"])
    for module, count in sorted(module_totals.items()):
        lines.append(f"| `{module}` | {count} | {module_reason[module]} |")
    lines.append("")
    lines.append("### Classes in the class-level scope")
    lines.append("")
    lines.append("| Class | Reason |")
    lines.append("|---|---|")
    for record in sorted(records, key=lambda r: r["fqcn"]):
        if record["status"] == "excluded" and record["inClassScope"]:
            lines.append(f"| `{record['fqcn']}` | {record['reason']} |")
    lines.append("")
    lines.append("## Pending / 待完成")
    lines.append("")
    pending_by_module: dict[str, list[str]] = defaultdict(list)
    for record in records:
        if record["status"] == "pending":
            pending_by_module[record["module"]].append(record["fqcn"])
    for module, names in sorted(pending_by_module.items()):
        lines.append(f"### `{module}` ({len(names)})")
        lines.append("")
        for name in sorted(names):
            lines.append(f"- [ ] `{name}`")
        lines.append("")
    lines.append("## Regenerating / 重新生成")
    lines.append("")
    lines.append("```powershell")
    lines.append("python scripts/hutool_inventory.py --hutool <hutool clone>")
    lines.append("python scripts/hutool_coverage.py --hutool <hutool clone>")
    lines.append("```")
    lines.append("")
    return "\n".join(lines)


def site_page(records: list[dict], meta: dict, lang: str) -> str:
    """The documentation-site version of the report: totals, module table and exclusion reasons."""
    by_module: dict[str, list[dict]] = defaultdict(list)
    for record in records:
        by_module[record["module"]].append(record)
    counts = Counter(record["status"] for record in records)
    in_scope = len(records) - counts["excluded"]
    percent = counts["done"] / max(1, in_scope) * 100

    if lang == "zh":
        title = "功能覆盖率"
        intro = [
            "这份清单由 `scripts/hutool_coverage.py` 自动生成，统计口径与 `docs/development/coverage.md` 一致：",
            "上游一个 Java 类算一项，Kotlin 源码里带 `@hutool <全限定类名>` 的 KDoc 标签算已完成，",
            "明确排除的项必须带理由。页面上的数字每次运行脚本都会重算，不手工维护。",
        ]
        labels = ("模块", "总数", "已完成", "明确不做", "待完成", "排除理由")
    else:
        title = "Coverage"
        intro = [
            "This report is generated by `scripts/hutool_coverage.py`; `docs/development/coverage.md` "
            "holds the same data with the full per-class list.",
            "One upstream Java class is one item. A Kotlin file tagged `@hutool <fqcn>` in its KDoc "
            "counts as done, and every exclusion carries a reason. The numbers below are recomputed "
            "on each run and are never typed by hand.",
        ]
        labels = ("Module", "Total", "Done", "Excluded", "Pending", "Reason")

    lines: list[str] = ["---", "sidebar_position: 3", f"title: {title}", "---", "", f"# {title}", ""]
    lines.extend(intro)
    lines.append("")
    lines.append(
        f"**{counts['done']} / {in_scope}** ({percent:.1f}%)"
        if lang == "en"
        else f"**{counts['done']} / {in_scope}**（{percent:.1f}%）"
    )
    lines.append("")
    lines.append(f"| {labels[0]} | {labels[1]} | {labels[2]} | {labels[3]} | {labels[4]} |")
    lines.append("|---|---:|---:|---:|---:|")
    for module, module_records in sorted(by_module.items()):
        module_counts = Counter(record["status"] for record in module_records)
        lines.append(
            f"| `{module}` | {len(module_records)} | {module_counts['done']} | "
            f"{module_counts['excluded']} | {module_counts['pending']} |"
        )
    lines.append(
        f"| **{'总计' if lang == 'zh' else 'total'}** | **{len(records)}** | **{counts['done']}** | "
        f"**{counts['excluded']}** | **{counts['pending']}** |"
    )
    lines.append("")
    heading = "明确不做的模块" if lang == "zh" else "Excluded modules"
    lines.append(f"## {heading}")
    lines.append("")
    lines.append(f"| {labels[0]} | {labels[1]} | {labels[5]} |")
    lines.append("|---|---:|---|")
    module_reason: dict[str, str] = {}
    for record in records:
        if not record["inClassScope"]:
            module_reason[record["module"]] = record["reason"]
    module_totals = Counter(record["module"] for record in records if not record["inClassScope"])
    for module, count in sorted(module_totals.items()):
        lines.append(f"| `{module}` | {count} | {module_reason[module]} |")
    lines.append("")
    lines.append(
        "完整清单（每个类、每条排除理由）：[docs/development/coverage.md]"
        "(https://github.com/shijianjs/kmp-hutool/blob/main/docs/development/coverage.md)"
        if lang == "zh"
        else "The full list, class by class with every reason, is in "
        "[docs/development/coverage.md]"
        "(https://github.com/shijianjs/kmp-hutool/blob/main/docs/development/coverage.md)."
    )
    lines.append("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--hutool", default=r"S:\workspace\github\chinabugotech\hutool")
    parser.add_argument("--sources", default=".")
    parser.add_argument("--inventory", default="build/hutool-inventory.json")
    parser.add_argument("--exclusions", default="scripts/hutool-exclusions.json")
    parser.add_argument("--out", default="docs/development/coverage.md")
    parser.add_argument("--json-out", default="build/hutool-coverage.json")
    parser.add_argument("--check", action="store_true", help="print the totals only")
    args = parser.parse_args()

    hutool_root = Path(args.hutool)
    if not (hutool_root / "pom.xml").is_file():
        print(f"not a hutool clone: {hutool_root}", file=sys.stderr)
        return 1

    inventory_path = Path(args.inventory)
    if inventory_path.is_file():
        items = json.loads(inventory_path.read_text(encoding="utf-8"))
    else:
        items = hutool_inventory.collect(hutool_root)

    source_roots = [path for path in Path(args.sources).glob("*/src/commonMain/kotlin") if path.is_dir()]
    done_tags, kt_files = scan_kotlin_sources(source_roots)

    exclusions = load_exclusions(Path(args.exclusions))
    exclusions["_root"] = str(hutool_root)
    records = classify(items, done_tags, exclusions)

    version = "unknown"
    pom = (hutool_root / "pom.xml").read_text(encoding="utf-8", errors="replace")
    match = re.search(r"<version>([^<]+)</version>", pom)
    if match:
        version = match.group(1)

    meta = {
        "hutool": str(hutool_root),
        "version": version,
        "date": __import__("datetime").date.today().isoformat(),
        "scopeModules": sorted(hutool_inventory.CLASS_SCOPE_MODULES),
        "kotlinFiles": kt_files,
        "taggedClasses": len(done_tags),
    }

    unknown_tags = sorted(set(done_tags) - {item["fqcn"] for item in items})
    if unknown_tags:
        print("tags that match no upstream class:", *unknown_tags, file=sys.stderr)

    counts = Counter(record["status"] for record in records)
    print(
        f"{len(records)} items: {counts['done']} done, {counts['excluded']} excluded, "
        f"{counts['pending']} pending ({kt_files} Kotlin files scanned, {len(done_tags)} tagged classes)"
    )
    if args.check:
        return 0

    out = Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(markdown(records, meta), encoding="utf-8", newline="\n")

    json_out = Path(args.json_out)
    json_out.parent.mkdir(parents=True, exist_ok=True)
    json_out.write_text(
        json.dumps({"meta": meta, "items": records}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(f"written to {out} and {json_out}")

    site_en = ROOT / "docs/src/jsMain/resources/docs/guide/coverage.md"
    site_zh = (
        ROOT / "docs/src/jsMain/resources/i18n/zh-Hans/docusaurus-plugin-content-docs/current/guide/coverage.md"
    )
    for path, lang in ((site_en, "en"), (site_zh, "zh")):
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(site_page(records, meta, lang), encoding="utf-8", newline="\n")
    print(f"written to {site_en} and {site_zh}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
