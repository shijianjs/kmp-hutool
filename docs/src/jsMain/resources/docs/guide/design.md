---
sidebar_position: 2
title: Design
---

# Design

The port follows three rules: keep Hutool's behaviour, use Kotlin where Kotlin is better, and never
carry a Java dependency into `commonMain`.

## Merged facades

Hutool splits some utilities in two: `StrUtil` and `CharSequenceUtil`, `CollUtil` and
`CollectionUtil`, `NumberUtil` and `NumberUtil` subclasses. In Kotlin an object with overloads costs
nothing, so the port merges them:

| Port | Upstream classes it replaces |
| --- | --- |
| `core.util.StrUtil` | `CharSequenceUtil` + `StrUtil` |
| `core.collection.CollUtil` | `CollUtil` + `CollectionUtil` |
| `core.util.IdUtil` | `IdUtil` + the nested `Snowflake` (now top-level `Snowflake`) |

Both KDoc tags stay on the merged file (`@hutool cn.hutool.core.util.CharSequenceUtil`,
`@hutool cn.hutool.core.util.StrUtil`), so coverage counts source classes, not port files.

## Kotlin instead of Java idioms

* **`text` results are `String`**, not `CharSequence`. Upstream returns `CharSequence` to avoid
  copies; Kotlin call sites immediately call `toString()` anyway.
* **`vararg` replaces array parameters** where upstream takes `String...`,
  and the port always forwards with the spread operator.
* **`MutableList`/`MutableMap` replace mutable beans.** `Dict` delegates to a `MutableMap`, so it
  works with `dict["key"]` and `dict.getStr("key")` alike.
* **`data class` for value types** (`Tuple`, `StopWatch.TaskInfo`, `Range` is a class with
  `contains`), giving `equals`/`hashCode`/`copy` for free.
* **`inline reified` for type-directed helpers** — `EnumUtil.getNames<E>()`,
  `CollUtil.distinct<T>()` — instead of passing a `Class` around.
* **Top-level functions where there is no state**, objects where Hutool has an object, and only
  classes for genuine state (`StrBuilder`, `StrJoiner`, `StopWatch`, `Validator`).

## What replaces Java-only APIs

| Upstream | Port |
| --- | --- |
| `java.util.Date`, `Calendar`, `SimpleDateFormat` | `kotlinx.datetime.LocalDate`, `Instant`, `TimeZone`, `LocalDateTime` |
| `java.math.BigDecimal` | `Double`/`Long` and explicit scale arguments (no arbitrary-precision decimal in common code) |
| `java.io.File`, streams, `Reader`/`Writer` | not ported; I/O is platform work, not a utility |
| reflection (`BeanUtil`, `ClassUtil`, `Convert`'s type registry) | explicit `Convert.toXxx(value)` functions |
| `Thread`, `ThreadLocal`, timers | not ported; coroutines are the Kotlin answer |
| `java.util.stream` | Kotlin collection operations |
| AWT/Swing image helpers, `javax.tools` | not ported |
| `Formatter`'s `Filter` overload | not ported; format via `StrFormatter` |
| GBK and other JVM-only charsets | not ported; UTF-8 and `\uXXXX` escapes only |

`Validator` throws `ValidateException`, which extends `IllegalArgumentException` exactly like
upstream's `cn.hutool.core.lang.Validator` exceptions, so callers can catch either.

## Packages

`commonMain` mirrors the upstream package tree under `io.github.shijianjs.kmphutool`:

| Port package | Upstream package |
| --- | --- |
| `core.util` | `cn.hutool.core.util` |
| `core.text` | `cn.hutool.core.text` |
| `core.collection` | `cn.hutool.core.collection` |
| `core.map` | `cn.hutool.core.map` |
| `core.lang` | `cn.hutool.core.lang` |
| `core.codec` | `cn.hutool.core.codec` |
| `core.convert` | `cn.hutool.core.convert` |
| `core.comparator` | `cn.hutool.core.comparator` |
| `core.date` | `cn.hutool.core.date` |
| `core.math` | `cn.hutool.core.math` |
| `core.exceptions` | `cn.hutool.core.exceptions` |

## Optional dependencies {#optional-dependencies}

The core is deliberately dependency-light: only `kotlinx-datetime`. Larger areas of Hutool need
libraries that have their own Kotlin Multiplatform implementations, and bundling them would force
every consumer to ship them. When a ported feature needs one, the module page names the package and
the consumer adds it. Nothing is transitive.

## Build and CI

Local network workarounds must not leak into the repository:

* Maven mirrors live in `~/.gradle/init.d/*.gradle.kts`, npm mirrors in `~/.npmrc`.
* `settings.gradle.kts` pins the Kotlin and kotlinx groups to `mavenCentral()` with
  `exclusiveContent`, so the build never depends on a mirror being present.
* GitHub Actions runs against the public registries, exactly like a fresh clone.

## Coverage

`scripts/hutool_inventory.py` walks a local Hutool clone and writes one item per Java source file
(1557 in total) with its module and public-member count. `scripts/hutool_coverage.py` then:

1. reads every `@hutool <fqcn>` KDoc tag under `*/src/commonMain/kotlin` to mark classes as done;
2. applies `scripts/hutool-exclusions.json` — whole modules and individual classes that cannot work
   in common code — and records the reason;
3. treats a class as excluded when its public API is only JDK types (the "husk" rule);
4. writes [coverage.md](coverage.md) and `build/hutool-coverage.json`.

The generated page is the source of truth for numbers; no count is typed by hand.
