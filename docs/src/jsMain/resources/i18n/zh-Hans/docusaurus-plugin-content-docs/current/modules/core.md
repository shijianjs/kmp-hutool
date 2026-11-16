---
sidebar_position: 1
title: core
---

# kmp-hutool-core

本库目前唯一发布的模块。下面所有内容都在 `commonMain`，因此 JVM、JS、Wasm 都能编译，并在 JVM 与 Node.js 上跑测试。

## 已实现的类

| 包 | 类 |
| --- | --- |
| `core.exceptions` | `UtilException`、`ValidateException` |
| `core.text` | `StrPool`、`StrFormatter`、`StrSplitter`、`StrJoiner`、`StrBuilder`、`UnicodeUtil` |
| `core.util` | `CharUtil`、`StrUtil`、`EscapeUtil`、`ObjectUtil`、`ArrayUtil`、`NumberUtil`、`RandomUtil`、`HexUtil`、`ReUtil`、`IdUtil`（含 `Snowflake`）、`PageUtil`、`EnumUtil`、`DesensitizedUtil`、`PhoneUtil`、`CreditCodeUtil`、`IdcardUtil` |
| `core.collection` | `ListUtil`、`IterUtil`、`CollUtil`、`Partition` |
| `core.map` | `MapUtil`、`CaseInsensitiveMap` |
| `core.lang` | `Dict`、`Validator`、`Assert`、`Range`、`Holder`、`MutableObj`、`WeightRandom`、`Tuple` |
| `core.codec` | `Base64` |
| `core.convert` | `Convert`、`BasicType` |
| `core.comparator` | `CompareUtil`、`VersionComparator` |
| `core.date` | `StopWatch` |
| `core.math` | `MathUtil` |

每个文件的 KDoc 都带 `@hutool <上游全限定类名>` 标签，覆盖率脚本按标签计数，一个类只算一次。合并门面带两个标签（`StrUtil` 覆盖 `CharSequenceUtil` 与 `StrUtil`，`CollUtil` 覆盖 `CollUtil` 与 `CollectionUtil`）。

## 典型用法

```kotlin
// 文本
StrUtil.format("{} + {} = {}", 1, 2, 3)
StrUtil.splitTrim("a, b ,c", ",")
StrFormatter.format("你好 {name}！", mapOf("name" to "Kotlin"))

// 数字
NumberUtil.round(3.14159, 2)
NumberUtil.format(1234567.891, 2)

// 集合
CollUtil.join(listOf("a", "b"), ", ")
CollUtil.distinct(listOf(1, 1, 2))
ListUtil.partition(listOf(1, 2, 3, 4, 5), 2)

// 无反射的字典
val dict = Dict.of("name" to "Ada", "age" to 36)
dict.getStr("name")
dict.getInt("age")

// 校验
Validator.isEmail("a@b.com")
Validator.notEmpty("value", "value 不能为空")

// 编码与 ID
Base64.encode("kmp-hutool")
IdUtil.getSnowflakeNextId()
```

## 日期时间

Hutool 的 `DateUtil` 没有作为类移植：日期统一是 `kotlinx.datetime` 的值类型，`kotlinx-datetime` 也是本模块唯一依赖。已实现的部分包括 `DateUnit`/`DateField`/`Month`/`Week`/`Quarter`、`YearQuarter`、`BetweenFormatter`/`TimeInterval`、`DatePattern` 常量，以及整个 `date.chinese` 包（`LunarInfo`、`ChineseMonth`、`SolarTerms`、`GanZhi`、`LunarFestival`）和 `ChineseDate`：

```kotlin
val lunar = ChineseDate(2024, 1, 1) // 对应公历 2024-02-10
lunar.getChineseMonthName()          // 正月
lunar.getGregorianDate()             // 2024-02-10
lunar.getTerm()                      // 节气，当天没有节气时为空串

BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format() // 1分30秒
```

`DateUtil` 中格式化/解析部分计划以扩展函数形式提供在 `LocalDate`、`LocalDateTime`、`Instant` 上；需要 pattern 引擎时留在 `kmp-hutool-core`，不再引入格式化库。

## 更大功能所需的可选依赖

| 领域 | 上游模块 | 本库做法 |
| --- | --- | --- |
| JSON | `hutool-json` | 不打进本库；用 `kotlinx-serialization-json` 配合 `core.convert` |
| HTTP | `hutool-http` | 不打进本库；用 Ktor |
| 加密 | `hutool-crypto` | 不打进本库；用平台 API 或 KMP 加密库 |
| 日志 | `hutool-log` | 不打进本库 |
| 数据库、POI、Servlet、Cron、Cache | `hutool-db`、`hutool-poi` 等 | 排除，逐模块理由见[覆盖率页](../guide/coverage.md) |

规则一致：核心不会替使用方加任何依赖。

## 不可移植

反射类工具（`BeanUtil`、`ClassUtil`、`Getter`、注解扫描）、`java.io`/`java.nio` I/O、线程与定时器、AWT/Swing 图片、`javax.tools`、`BigDecimal`、`java.util.stream` 在 common 代码里没有对应实现。它们被记录在 `scripts/hutool-exclusions.json` 并附具体理由，而不是悄悄消失。
