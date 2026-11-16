---
sidebar_position: 1
title: 快速开始
---

# 快速开始

## 环境要求

- 使用方项目的 Kotlin 版本 2.2 以上（本库用 Kotlin 2.4.21 开发）。
- 已发布的目标平台：`jvm`、`js`（IR）、`wasmJs`。
- 测试用 `kotlin("test")`，同一套用例在各目标上都能跑。

## 加依赖

```kotlin
kotlin {
    jvm()
    js { nodejs() }
    wasmJs { nodejs() }
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.shijianjs:kmp-hutool-core:5.8.48-kt.1")
        }
    }
}
```

版本号写在 `gradle.properties` 的 `kmp-hutool.version`。`-kt.N` 后缀表示对上游 `5.8.48` 的第 N 次 Kotlin 改写；上游换版本时重新从 `5.8.x-kt.1` 开始。

## 第一个例子

包结构镜像 Hutool（`core.util`、`core.text`、`core.collection`、`core.codec`……），根包是 `io.github.shijianjs.kmphutool`：

```kotlin
import io.github.shijianjs.kmphutool.core.util.StrUtil
import io.github.shijianjs.kmphutool.core.util.NumberUtil
import io.github.shijianjs.kmphutool.core.collection.CollUtil

fun main() {
    println(StrUtil.format("{} has {} items", "cart", 3))
    println(StrUtil.upperFirst("kotlin"))        // Kotlin
    println(NumberUtil.format(1_234_567.891, 2)) // 1,234,567.89
    println(CollUtil.join(listOf(1, 2, 3), "-")) // 1-2-3
}
```

Kotlin 写法更顺手的地方，同时也提供扩展函数风格：

```kotlin
val parts = "a, b ,c".splitTrim(",")
val code = "42".toIntOrNull() ?: 0
```

## 可选依赖

`kmp-hutool-core` 只依赖 `kotlinx-datetime`，且只用于日期类型。核心之外的大块功能（JSON、HTTP、加密、数据库、POI……）**不打进本库**：某个功能需要 kotlinx 系列或其它 KMP 包时，由使用方自己加依赖，`commonMain` 不会隐式传递。哪些区域将来需要哪个包，见[模块页](../modules/core.md)。

## 从源码构建

```bash
./gradlew :kmp-hutool-core:jvmTest   # JVM 单元测试
./gradlew :kmp-hutool-core:jsTest    # 同一套测试在 Node.js 上跑
./gradlew :docs:docusaurusBuild      # 本文档站
./gradlew pagesSite                  # 文档 + 演示 + API，GitHub Pages 用的目录结构
```

网络慢时的镜像配置放在**自己的** `~/.gradle/init.d/*.gradle.kts` 与 `~/.npmrc`，仓库里的构建脚本必须指向公共仓库，保证 CI 可复现。更多说明见[设计](design.md)。
