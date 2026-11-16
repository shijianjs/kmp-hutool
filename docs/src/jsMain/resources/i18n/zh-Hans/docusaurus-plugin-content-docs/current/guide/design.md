---
sidebar_position: 2
title: 设计
---

# 设计

移植遵循三条原则：保留 Hutool 的行为、Kotlin 更自然的地方改用 Kotlin、`commonMain` 绝不夹带 Java 依赖。

## 合并门面

Hutool 有些工具被拆成两个类，比如 `StrUtil` 与 `CharSequenceUtil`、`CollUtil` 与 `CollectionUtil`。Kotlin 的 object 加重载没有额外成本，于是直接合并：

| 本库 | 覆盖的上游类 |
| --- | --- |
| `core.util.StrUtil` | `CharSequenceUtil` + `StrUtil` |
| `core.collection.CollUtil` | `CollUtil` + `CollectionUtil` |
| `core.util.IdUtil` | `IdUtil` + 内部类 `Snowflake`（现在是顶层 `Snowflake`） |

合并后的文件保留两个 `@hutool` 标签，覆盖率按上游类计数，不按文件数。

## 用 Kotlin 写法取代 Java 习惯

* 文本参数与返回值用 `String`，不用 `CharSequence`：上游为避免复制返回 `CharSequence`，调用方却几乎立刻 `toString()`。
* 上游的 `String...` 一律改成 `vararg`，转发时记得展开。
* 可变 JavaBean 换成 `MutableList`/`MutableMap`：`Dict` 委托给 `MutableMap`，`dict["key"]` 与 `dict.getStr("key")` 都能用。
* 值类型用 `data class`（`Tuple`、`StopWatch.TaskInfo`），`equals`/`hashCode`/`copy` 白拿。
* 需要类型导向的辅助用 `inline reified`（`EnumUtil.getNames<E>()`、`CollUtil.distinct<T>()`），不再传 `Class`。
* 无状态的放顶层函数或 object；只有真正有状态的才建类（`StrBuilder`、`StrJoiner`、`StopWatch`、`Validator`）。

## Java 专属 API 的替代

| 上游 | 本库 |
| --- | --- |
| `java.util.Date`、`Calendar`、`SimpleDateFormat` | `kotlinx.datetime` 的 `LocalDate`、`Instant`、`TimeZone`、`LocalDateTime` |
| `java.math.BigDecimal` | `Double`/`Long` 加显式 scale 参数（common 代码没有任意精度十进制） |
| `java.io.File`、流、`Reader`/`Writer` | 不移植，I/O 属于平台职责 |
| 反射（`BeanUtil`、`ClassUtil`、`Convert` 的类型注册表） | 显式 `Convert.toXxx(value)` |
| `Thread`、`ThreadLocal`、定时器 | 不移植，Kotlin 侧交给协程 |
| `java.util.stream` | Kotlin 集合操作 |
| AWT/Swing 图片、`javax.tools` | 不移植 |
| `Formatter` 的 `Filter` 重载 | 不移植，格式化走 `StrFormatter` |
| GBK 等 JVM 专属字符集 | 不移植，只保留 UTF-8 与 `\uXXXX` |

`Validator` 抛 `ValidateException`，与上游异常一样是 `IllegalArgumentException` 的子类，调用方按任一类型捕获都行。

## 包结构

`commonMain` 在 `io.github.shijianjs.kmphutool` 下镜像上游包树：

| 本库包 | 上游包 |
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

## 可选依赖 {#optional-dependencies}

核心刻意保持轻依赖：只有 `kotlinx-datetime`。Hutool 里较大的区域需要各自的 Kotlin Multiplatform 实现，打进本库会强迫所有使用方一起背。需要时由使用方加包，模块页会写明对应哪一块。没有任何传递依赖。

## 构建与 CI

本地网络变通不能泄漏进仓库：

* Maven 镜像放 `~/.gradle/init.d/*.gradle.kts`，npm 镜像放 `~/.npmrc`。
* `settings.gradle.kts` 用 `exclusiveContent` 把 Kotlin 与 kotlinx 各组钉到 `mavenCentral()`，构建不依赖任何镜像存在。
* GitHub Actions 直接访问公共仓库，与全新 clone 一致。

## 覆盖率

`scripts/hutool_inventory.py` 遍历本地 Hutool 克隆，一个 Java 源文件算一项（共 1557 项），记录所属模块与 public 成员数。`scripts/hutool_coverage.py` 随后：

1. 扫描 `*/src/commonMain/kotlin` 里所有 `@hutool <全限定类名>` KDoc 标签，标记为已完成；
2. 应用 `scripts/hutool-exclusions.json`：整模块排除与单类排除都带理由；
3. 用 "husk 规则" 判定：public API 全是 JDK 类型的类也算排除；
4. 生成 [coverage.md](coverage.md) 与 `build/hutool-coverage.json`。

页面数字以脚本为准，不存在手工维护的计数。
