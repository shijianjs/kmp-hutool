---
sidebar_position: 1
title: core
---

# kmp-hutool-core

The only published module. Everything below lives in `commonMain`, so it compiles for `jvm`, `js` and
`wasmJs` and is tested on JVM and Node.js.

## Implemented so far

| Package | Classes |
| --- | --- |
| `core.exceptions` | `UtilException`, `ValidateException` |
| `core.text` | `StrPool`, `StrFormatter`, `StrSplitter`, `StrJoiner`, `StrBuilder`, `UnicodeUtil` |
| `core.util` | `CharUtil`, `StrUtil`, `EscapeUtil`, `ObjectUtil`, `ArrayUtil`, `NumberUtil`, `RandomUtil`, `HexUtil`, `ReUtil`, `IdUtil` (+ `Snowflake`), `PageUtil`, `EnumUtil`, `DesensitizedUtil`, `PhoneUtil`, `CreditCodeUtil`, `IdcardUtil` |
| `core.collection` | `ListUtil`, `IterUtil`, `CollUtil`, `Partition` |
| `core.map` | `MapUtil`, `CaseInsensitiveMap` |
| `core.lang` | `Dict`, `Validator`, `Assert`, `Range`, `Holder`, `MutableObj`, `WeightRandom`, `Tuple` |
| `core.codec` | `Base64` |
| `core.convert` | `Convert`, `BasicType` |
| `core.comparator` | `CompareUtil`, `VersionComparator` |
| `core.date` | `StopWatch` |
| `core.math` | `MathUtil` |

Each file carries a `@hutool <fully-qualified upstream class>` KDoc tag; that tag is what the
coverage script reads, so a ported class is counted exactly once. Merged facades carry two tags
(`StrUtil` covers `CharSequenceUtil` and `StrUtil`, `CollUtil` covers `CollUtil` and
`CollectionUtil`).

## Typical use

```kotlin
// text
StrUtil.format("{} + {} = {}", 1, 2, 3)
StrUtil.splitTrim("a, b ,c", ",")
StrFormatter.format("Hello {name}!", mapOf("name" to "Kotlin"))

// numbers
NumberUtil.round(3.14159, 2)
NumberUtil.format(1234567.891, 2)
NumberUtil.isNumber("1e3")

// collections
CollUtil.join(listOf("a", "b"), ", ")
CollUtil.distinct(listOf(1, 1, 2))
ListUtil.partition(listOf(1, 2, 3, 4, 5), 2)

// maps and beans without reflection
val dict = Dict.of("name" to "Ada", "age" to 36)
dict.getStr("name")
dict.getInt("age")

// validation
Validator.isEmail("a@b.com")
Validator.notEmpty("value", "value must not be empty")

// codecs and ids
Base64.encode("kmp-hutool")
IdUtil.getSnowflakeNextId()
```

## Date and time

Hutool's `DateUtil` is not ported as a class: dates are `kotlinx.datetime` values, and the
`kotlinx-datetime` package is the only dependency of this module. The implemented date surface is
`DateUnit`/`DateField`/`Month`/`Week`/`Quarter`, `YearQuarter`, `BetweenFormatter`/`TimeInterval`,
`DatePattern` constants, plus the whole `date.chinese` package (`LunarInfo`, `ChineseMonth`,
`SolarTerms`, `GanZhi`, `LunarFestival`) and `ChineseDate`:

```kotlin
val lunar = ChineseDate(2024, 1, 1) // Gregorian 2024-02-10
lunar.getChineseMonthName()          // 正月
lunar.getGregorianDate()             // 2024-02-10
lunar.getTerm()                      // 节气, empty when the day has none

BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format() // 1分30秒
```

The formatting/parsing part of `DateUtil` is planned as extension functions on `LocalDate`,
`LocalDateTime` and `Instant`; when a helper needs a pattern engine it will stay in
`kmp-hutool-core` rather than pulling in a formatting library.

## Optional dependencies for larger areas

| Area | Upstream module | What this port does |
| --- | --- | --- |
| JSON | `hutool-json` | not bundled; use `kotlinx-serialization-json` and the `core.convert` helpers |
| HTTP | `hutool-http` | not bundled; use Ktor |
| Crypto | `hutool-crypto` | not bundled; use platform APIs or a KMP crypto library |
| Logging | `hutool-log` | not bundled |
| Database, POI, Servlet, Cron, Cache | `hutool-db`, `hutool-poi`, ... | excluded; see the [coverage page](../guide/coverage.md) for the per-module reason |

The rule is the same everywhere: the core never adds a dependency on your behalf.

## Not portable

Reflection-based helpers (`BeanUtil`, `ClassUtil`, `Getter`, annotation scanning), `java.io`/`java.nio`
I/O, thread and timer utilities, AWT/Swing image helpers, `javax.tools`, `BigDecimal`, and
`java.util.stream` have no equivalent in common code. They are recorded as excluded with a reason in
`scripts/hutool-exclusions.json` instead of being silently dropped.
