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
| `core.util` | `CharUtil`、`StrUtil`、`EscapeUtil`、`ObjectUtil`、`ArrayUtil`、`NumberUtil`、`RandomUtil`、`HexUtil`、`HashUtil`、`ReUtil`、`IdUtil`（含 `Snowflake`）、`PageUtil`、`EnumUtil`、`DesensitizedUtil`、`PhoneUtil`、`CreditCodeUtil`、`IdcardUtil`、`ObjUtil` |
| `core.collection` | `ListUtil`、`IterUtil`、`CollUtil`、`Partition`、`RandomAccessPartition`、`RandomAccessAvgPartition` |
| `core.map` | `MapUtil`、`CaseInsensitiveMap`、`TableMap`、`MapProxy` |
| `core.lang` | `Dict`、`Validator`、`Assert`、`Range`、`Holder`、`MutableObj`、`WeightRandom`、`Tuple`、`Opt`、`ConsoleTable`、`Pair`、`RegexPool`、`PatternPool`、`CityHash` |
| `core.codec` | `Base64`、`PunyCode`、`Morse`、`Base85` |
| `core.convert` | `Convert`、`BasicType`、`NumberWordFormatter`、`NumberChineseFormatter`、`NumberWithFormat`、`Converter`、`TypeConverter`、`CastUtil`、`ConvertException` |
| `core.comparator` | `CompareUtil`、`VersionComparator` |
| `core.date` | `DateUnit`、`DateField`、`Month`、`Week`、`Quarter`、`Zodiac`、`DatePattern`、`DateBetween`、`StopWatch`、`BetweenFormatter`、`GroupTimeInterval`、`TimeInterval`、`YearQuarter`、`ChineseDate`、`TimeZoneUtil`、`ZoneUtil`、`DateBasic`，以及 `date.chinese` |
| `core.math` | `MathUtil`、`CRC16Checksum`、`DataSize` |
| `core.crypto` | `Vigenere`、`CipherMode`、`Mode`、`Padding`、`CryptoException`、算法名枚举（`DigestAlgorithm`、`HmacAlgorithm`、`AsymmetricAlgorithm`、`SignAlgorithm`、`SymmetricAlgorithm`），以及纯 Kotlin 的 `SM3`、`HOTP`、`TOTP`、`SignUtil` |
| `core.bloomfilter` | `BloomFilter`、`BitMapBloomFilter`、`BloomFilterUtil`、位图与十个哈希过滤器 |
| `core.io` | `FastByteBuffer`、`FastStringWriter`、`FastByteArrayOutputStream`、`NullOutputStream`、`StreamProgress`、`LineHandler`、`IORuntimeException` |
| `core.setting` | `GroupedMap`、`GroupedSet`、`SettingRuntimeException` |
| `core.thread.lock` | `NoLock`、`NoReadWriteLock` |
| `core.json` | `JSONString`、`JSONBeanParser`、`JSONException`、`JSONStrFormatter`、`JSON`、`JSONObject`、`JSONArray`、`JSONNull`、`JSONTokener`、`JSONParser`、`JSONObjectIter`、`JSONSupport`、四个序列化接口、`XML`、`XMLTokener`、`InternalJSONUtil`，以及 `xml/{JSONXMLParser,JSONXMLSerializer,ParseConfig}` |
| `core.jwt` | `JWTException`、`JWTSigner`、`NoneJWTSigner`、`AlgorithmUtil`、`Claims`、`RegisteredPayload`、`JWTHeader`、`JWTPayload`、`JWT`、`JWTValidator`、`JWTUtil` |
| `core.cache` | `CacheListener`、`Cache`、`CacheObj`、`AbstractCache`、`FIFOCache`、`LFUCache`、`LRUCache`、`NoCache`、`CacheUtil` |
| `core.cron` | `CronException`、`CronConfig`、`Task`、`CronTask`、`Part`、七个 `PartMatcher` 实现、`PartParser`、`PatternParser`、`CronPattern`、`CronPatternBuilder` |
| `core.dfa` | `WordTree`、`FoundWord`、`StopChar`、`SensitiveProcessor` |

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

Hutool 的 `DateUtil` 没有作为类移植：日期统一是 `kotlinx.datetime` 的值类型。本模块依赖
`kotlinx-datetime` 处理日期，依赖 `kotlinx-serialization-json` 提供 JSON 值模型。已实现的部分包括 `DateUnit`/`DateField`/`Month`/`Week`/`Quarter`、`YearQuarter`、`BetweenFormatter`/`TimeInterval`、`DatePattern` 常量，以及整个 `date.chinese` 包（`LunarInfo`、`ChineseMonth`、`SolarTerms`、`GanZhi`、`LunarFestival`）和 `ChineseDate`：

```kotlin
val lunar = ChineseDate(2024, 1, 1) // 对应公历 2024-02-10
lunar.getChineseMonthName()          // 正月
lunar.getGregorianDate()             // 2024-02-10
lunar.getTerm()                      // 节气，当天没有节气时为空串

BetweenFormatter(90_000L, BetweenFormatter.Level.SECOND).format() // 1分30秒
```

`TimeZoneUtil` 通过 `kotlinx.datetime.TimeZone` 解析 ID，未知 ID 回退 GMT；`ZoneUtil` 保留
Hutool 的 `toTimeZone`/`toZoneId` 入口，但在 common 只有一种时区类型，所以是恒等转换。
`DateBasic` 用 Kotlin 属性暴露 `pattern`、`timeZone`、`localeTag`；`localeTag` 是 `zh-CN` 这样的
IETF 语言标签，因为 `java.util.Locale` 在 common 没有对应类型。

## 缓存

`hutool-cache` 实现在 `core.cache`，使用 Kotlin 属性与 `() -> V` 取值函数。所有缓存都是
**单线程、非线程安全**的；多个协程共享时请在外层加平台锁或 actor。

```kotlin
val cache = CacheUtil.newLRUCache<String, String>(3, 1_000L) // 容量 3，超时 1 秒
cache.put("a", "A")
cache.get("a", true)                  // true 表示更新 LRU 的最后访问顺序
cache.get("missing") { "computed" }   // 取值函数的结果会被缓存
cache.size()
```

FIFO 淘汰最早插入的项，LFU 淘汰访问次数最少的项，LRU 淘汰最久未访问的项，`NoCache` 只在当前
调用内保留值。`CacheListener` 会收到 `onRemove`/`onClear` 回调。`WeakCache` 仍排除，因为 common
Kotlin 没有弱引用。

## Cron

表达式引擎是纯 common 实现。5 段表示 `分 时 日 月 周`，6 段增加秒，7 段再增加年；只有
`matchSecond` 为 true 时才匹配秒。

```kotlin
val pattern = CronPattern("*/5 9-17 * * mon-fri")   // 工作日 9:00-17:59，每 5 分钟
pattern.match(LocalDateTime(2026, 11, 17, 10, 5), isMatchSecond = false)

CronPatternBuilder.of()
    .setRange(Part.MINUTE, 0, 30)
    .setValues(Part.HOUR, 8, 12)
    .build()                                       // "0-30 8,12 * * *"
```

`Task` 是 `fun interface`；`CronTask` 把它与 id、`CronPattern` 组合。上游基于 `Calendar` 的
`nextMatchAfter` 没有移植：调度器自己推进时钟并调用 `match`，这样引擎不依赖
`java.util.Calendar`/`TimeZone`。

## JSON

`core.json` 保留 Hutool 的动态 API（`JSONObject`、`JSONArray`、`JSONParser`），但内部用普通
Kotlin 值存储，不再依赖反射。解析与输出走 `kotlinx-serialization-json`，这只是实现细节，使用方
不需要开启序列化编译器插件。

```kotlin
val user = JSONObject("""{"name":"Ada","tags":["kotlin","kmp"]}""")
user.getStr("name")                 // Ada
user.getJSONArray("tags")?.getStr(0) // kotlin
user.put("active", true).toJSONString()

val parsed = JSONParser.of(JSONTokener("""[1,2,3]""")).parse() as JSONArray
```

`JSONSupport` 会保存解析后的 JSON 并提供 `toJSON`/`toJSONString`；上游 `parse(JSON)` 的反射字段
拷贝在 common Kotlin 不可用。需要类型化 Bean 时请直接用 `kotlinx.serialization` 的
`@Serializable` 数据类。

XML 转换不依赖平台 XML 库：`XML.toJSONObject` 使用 `XMLTokener` 与 `JSONXMLParser`，
`XML.toXml` 使用 `JSONXMLSerializer`。文本放在 `content` 键下，属性与子元素共用命名空间，重复
标签会合并为 `JSONArray`。

```kotlin
val json = XML.toJSONObject("""<root id="1"><item>a</item><item>2</item></root>""")
json.getJSONObject("root")?.getJSONArray("item")?.getStr(0) // a
XML.toXml(json)                                             // <root>...</root>
```

## JWT

`JWT` 用 `Claims`（底层是 JSON 模型）拼出 `header.payload.signature`。签名算法由 `JWTSigner` 提供：
HMAC/RSA 实现留在平台侧，`NoneJWTSigner` 用于无签名 token。

```kotlin
val token = JWT.create()
    .setPayload("sub", "user-1")
    .setSigner(mySigner)          // 任意实现 sign/verify/getAlgorithm 的 JWTSigner
    .sign()

val parsed = JWT.of(token)
parsed.getPayload("sub")          // user-1
parsed.verify(mySigner)

JWTValidator.of(parsed).validateDate(Instant.parse("2026-11-17T00:00:00Z"))
```

`setExpiresAt`/`setNotBefore`/`setIssuedAt` 接收 `kotlinx.datetime.Instant`，内部按秒存储。
`JWTUtil` 提供 `createToken(payload, signer)`、`parseToken`、`verify`；上游接收字节数组的
便捷重载依赖其 HMAC 签名器工厂，这里有意不提供。

## 摘要与一次性密码

`SM3` 是纯 Kotlin 的国密摘要实现，`HOTP`/`TOTP` 分别实现 RFC 4226 与 RFC 6238，内置
HMAC-SHA1。其他 HMAC/加解密算法仍需平台 provider。

```kotlin
SM3().digestHex("abc")
    // 66C7F0F462EEEDD9D1F2D46BDC10E4E24167C4875CF2F7A2297DA02B8F4BA8E0

val totp = TOTP(key = "12345678901234567890".encodeToByteArray()) // 6 位，30 秒步进
totp.generate(Instant.parse("1970-01-01T00:00:59Z"))
totp.validate(Instant.parse("1970-01-01T00:00:59Z"), 1, 287082)

val secret = TOTP.generateGoogleSecretKey("alice@example.com", 20)
```


## 布隆过滤器

`hutool-bloomFilter` 整体可移植，位图过滤器在 `core.bloomfilter`：`BitMapBloomFilter` 组合五个哈希过滤器，`BloomFilterUtil.createBitMap` 是工厂方法，每个 `AbstractFilter` 子类也能单独使用。唯一没移植的是 `BitSetBloomFilter`，因为 `java.util.BitSet` 在 common 没有对应实现。

```kotlin
val filter = BitMapBloomFilter(5)      // m = 5，每个过滤器 8 Mbit
filter.add("kmp-hutool")               // true，首次加入
filter.contains("kmp-hutool")          // true
filter.add("kmp-hutool")               // false，已存在

val custom = SDBMFilter(1_000)         // 单独使用 SDBM 哈希过滤器
```

## 更大功能所需的可选依赖

| 领域 | 上游模块 | 本库做法 |
| --- | --- | --- |
| JSON | `hutool-json` | 动态 `JSONObject`/`JSONArray` 模型与 XML 转换已在 core；类型化 Bean 用 `kotlinx-serialization-json` 数据类 |
| HTTP | `hutool-http` | 不打进本库；用 Ktor |
| 加密 | `hutool-crypto` | 算法名与异常在 core；摘要/加解密引擎仍交给平台 |
| 日志 | `hutool-log` | 不打进本库；在平台日志之上用 `kotlin-logging` |
| 数据库、POI、Servlet | `hutool-db`、`hutool-poi`、`hutool-extra` | 排除，逐模块理由见[覆盖率页](../guide/coverage.md) |

规则一致：核心不会替使用方加任何依赖。

## 不可移植

反射类工具（`BeanUtil`、`ClassUtil`、`Getter`、注解扫描）、`java.io`/`java.nio` I/O、线程与定时器、AWT/Swing 图片、`javax.tools`、`BigDecimal`、`java.util.stream` 在 common 代码里没有对应实现。它们被记录在 `scripts/hutool-exclusions.json` 并附具体理由，而不是悄悄消失。

### 参数签名与 CityHash

`SignUtil.signParamsMd5`、`signParamsSha1`、`signParamsSha256` 会按 key 字符串排序，以 `key=value`
拼接并追加末尾密钥，再做摘要。`CityHash.hash32/hash64/hash128` 为纯 Kotlin 实现，各长度区间
（0-16、17-32、33-64、65+）的输出均与上游 Java 一致。
