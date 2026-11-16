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
| `core.util` | `CharUtil`, `StrUtil`, `EscapeUtil`, `ObjectUtil`, `ArrayUtil`, `NumberUtil`, `RandomUtil`, `HexUtil`, `ReUtil`, `IdUtil` (+ `Snowflake`), `HashUtil`, `PageUtil`, `EnumUtil`, `DesensitizedUtil`, `PhoneUtil`, `CreditCodeUtil`, `IdcardUtil`, `ObjUtil` |
| `core.collection` | `ListUtil`, `IterUtil`, `CollUtil`, `Partition`, `RandomAccessPartition`, `RandomAccessAvgPartition` |
| `core.map` | `MapUtil`, `CaseInsensitiveMap`, `TableMap`, `MapProxy` |
| `core.lang` | `Dict`, `Validator`, `Assert`, `Range`, `Holder`, `MutableObj`, `WeightRandom`, `Tuple`, `Opt`, `ConsoleTable`, `Pair` |
| `core.codec` | `Base64`, `PunyCode`, `Morse`, `Base85` |
| `core.convert` | `Convert`, `BasicType`, `NumberWordFormatter`, `NumberChineseFormatter`, `NumberWithFormat`, `Converter`, `TypeConverter`, `CastUtil`, `ConvertException` |
| `core.comparator` | `CompareUtil`, `VersionComparator` |
| `core.date` | `DateUnit`, `DateField`, `Month`, `Week`, `Quarter`, `Zodiac`, `DatePattern`, `DateBetween`, `StopWatch`, `BetweenFormatter`, `GroupTimeInterval`, `TimeInterval`, `YearQuarter`, `ChineseDate`, `TimeZoneUtil`, `ZoneUtil`, `DateBasic`, plus `date.chinese` |
| `core.math` | `MathUtil`, `CRC16Checksum`, `DataSize` |
| `core.crypto` | `Vigenere`, `CipherMode`, `Mode`, `Padding`, `CryptoException`, algorithm name enums (`DigestAlgorithm`, `HmacAlgorithm`, `AsymmetricAlgorithm`, `SignAlgorithm`, `SymmetricAlgorithm`) |
| `core.bloomfilter` | `BloomFilter`, `BitMapBloomFilter`, `BloomFilterUtil`, bit maps and the ten hash filters |
| `core.io` | `FastByteBuffer`, `FastStringWriter`, `FastByteArrayOutputStream`, `NullOutputStream`, `StreamProgress`, `LineHandler`, `IORuntimeException` |
| `core.setting` | `GroupedMap`, `GroupedSet`, `SettingRuntimeException` |
| `core.thread.lock` | `NoLock`, `NoReadWriteLock` |
| `core.json` | `JSONString`, `JSONBeanParser`, `JSONException`, `JSONStrFormatter`, `JSON`, `JSONObject`, `JSONArray`, `JSONNull`, `JSONTokener`, `JSONParser`, `JSONObjectIter`, `JSONSupport`, and the four serializer interfaces |
| `core.jwt` | `JWTException`, `JWTSigner`, `NoneJWTSigner`, `AlgorithmUtil` (claim containers await the JSON model) |
| `core.cache` | `CacheListener`, `Cache`, `CacheObj`, `AbstractCache`, `FIFOCache`, `LFUCache`, `LRUCache`, `NoCache`, `CacheUtil` |
| `core.cron` | `CronException`, `CronConfig`, `Task`, `CronTask`, `Part`, the seven `PartMatcher` implementations, `PartParser`, `PatternParser`, `CronPattern`, `CronPatternBuilder` |
| `core.dfa` | `WordTree`, `FoundWord`, `StopChar`, `SensitiveProcessor` |

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

Hutool's `DateUtil` is not ported as a class: dates are `kotlinx.datetime` values. This module
depends on `kotlinx-datetime` for dates and `kotlinx-serialization-json` for the JSON value model. The implemented date surface is
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

`TimeZoneUtil` resolves an ID through `kotlinx.datetime.TimeZone`, falls back to GMT for unknown IDs,
and `ZoneUtil` keeps Hutool's `toTimeZone`/`toZoneId` entry points as identity conversions because
common code has a single zone type. `DateBasic` exposes `pattern`, `timeZone` and `localeTag` as Kotlin
properties; `localeTag` is an IETF language tag such as `zh-CN`, since `java.util.Locale` has no common
counterpart.

## Cache

`hutool-cache` is implemented in `core.cache` with Kotlin properties and a `() -> V` value supplier.
All caches are **single-threaded and not thread-safe**; use a platform lock or an actor around them
when several coroutines share one instance.

```kotlin
val cache = CacheUtil.newLRUCache<String, String>(3, 1_000L) // capacity 3, 1s timeout
cache.put("a", "A")
cache.get("a", true)                  // true = update last-access for LRU ordering
cache.get("missing") { "computed" }   // supplier result is cached
cache.size()
```

FIFO evicts the earliest inserted entry, LFU the least frequently used, LRU the least recently used,
and `NoCache` only keeps the value for the current call. A `CacheListener` receives
`onRemove`/`onClear` callbacks. `WeakCache` stays excluded because common Kotlin has no weak references.

## Cron

The cron expression engine is pure common code. Five fields mean `minute hour day month weekday`; six
fields add seconds; seven fields add a year. Seconds are only matched when `matchSecond` is true.

```kotlin
val pattern = CronPattern("*/5 9-17 * * mon-fri")   // every 5 min, 09:00-17:59, weekdays
pattern.match(LocalDateTime(2026, 11, 17, 10, 5), isMatchSecond = false)

CronPatternBuilder.of()
    .setRange(Part.MINUTE, 0, 30)
    .setValues(Part.HOUR, 8, 12)
    .build()                                       // "0-30 8,12 * * *"
```

`Task` is a `fun interface`; `CronTask` pairs it with an id and a `CronPattern`. Upstream's
`Calendar`-based `nextMatchAfter` is not ported: a scheduler advances its own clock and calls
`match`, which keeps the engine free of `java.util.Calendar`/`TimeZone`.

## JSON

`core.json` keeps Hutool's dynamic API (`JSONObject`, `JSONArray`, `JSONParser`) but stores ordinary
Kotlin values instead of using reflection. Parsing and writing go through
`kotlinx-serialization-json`, which is an implementation detail: consumers do not need the
serialization compiler plugin.

```kotlin
val user = JSONObject("""{"name":"Ada","tags":["kotlin","kmp"]}""")
user.getStr("name")                 // Ada
user.getJSONArray("tags")?.getStr(0) // kotlin
user.put("active", true).toJSONString()

val parsed = JSONParser.of(JSONTokener("""[1,2,3]""")).parse() as JSONArray
```

`JSONSupport` stores the parsed JSON and exposes `toJSON`/`toJSONString`; upstream's reflective
`parse(JSON)` field copy is not available in common Kotlin. For typed beans use
`kotlinx.serialization` with `@Serializable` classes instead.


## Bloom filters

`hutool-bloomFilter` is portable as a whole, so the bitmap filters live in `core.bloomfilter`:
`BitMapBloomFilter` combines five hash filters, `BloomFilterUtil.createBitMap` is the factory,
and every `AbstractFilter` subclass is usable on its own. `BitSetBloomFilter` is the only class
left out, because `java.util.BitSet` has no common counterpart.

```kotlin
val filter = BitMapBloomFilter(5)      // m = 5 → 8 Mbit per filter
filter.add("kmp-hutool")               // true, first insertion
filter.contains("kmp-hutool")          // true
filter.add("kmp-hutool")               // false, already present

val custom = SDBMFilter(1_000)         // one filter with the SDBM hash
```

## Optional dependencies for larger areas

| Area | Upstream module | What this port does |
| --- | --- | --- |
| JSON | `hutool-json` | dynamic `JSONObject`/`JSONArray` model is in core; use `kotlinx-serialization-json` data classes for typed beans, and `XML` helpers are still pending |
| HTTP | `hutool-http` | not bundled; use Ktor |
| Crypto | `hutool-crypto` | algorithm names and exceptions are in core; digest/cipher engines stay platform-specific |
| Logging | `hutool-log` | not bundled; use `kotlin-logging` on top of your platform logger |
| Database, POI, Servlet | `hutool-db`, `hutool-poi`, `hutool-extra` | excluded; see the [coverage page](../guide/coverage.md) for the per-module reason |

The rule is the same everywhere: the core never adds a dependency on your behalf.

## Not portable

Reflection-based helpers (`BeanUtil`, `ClassUtil`, `Getter`, annotation scanning), `java.io`/`java.nio`
I/O, thread and timer utilities, AWT/Swing image helpers, `javax.tools`, `BigDecimal`, and
`java.util.stream` have no equivalent in common code. They are recorded as excluded with a reason in
`scripts/hutool-exclusions.json` instead of being silently dropped.
