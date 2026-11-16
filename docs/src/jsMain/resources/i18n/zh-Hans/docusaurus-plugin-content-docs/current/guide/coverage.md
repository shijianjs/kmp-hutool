---
sidebar_position: 3
title: 功能覆盖率
---

# 功能覆盖率

这份清单由 `scripts/hutool_coverage.py` 自动生成，统计口径与 `docs/development/coverage.md` 一致：
上游一个 Java 类算一项，Kotlin 源码里带 `@hutool <全限定类名>` 的 KDoc 标签算已完成，
明确排除的项必须带理由。页面上的数字每次运行脚本都会重算，不手工维护。

**293 / 638**（45.9%）

| 模块 | 总数 | 已完成 | 明确不做 | 待完成 |
|---|---:|---:|---:|---:|
| `hutool-ai` | 58 | 0 | 58 | 0 |
| `hutool-all` | 2 | 0 | 2 | 0 |
| `hutool-aop` | 15 | 0 | 15 | 0 |
| `hutool-bloomFilter` | 22 | 0 | 0 | 22 |
| `hutool-cache` | 22 | 0 | 1 | 21 |
| `hutool-captcha` | 13 | 0 | 13 | 0 |
| `hutool-core` | 715 | 277 | 290 | 148 |
| `hutool-cron` | 41 | 0 | 12 | 29 |
| `hutool-crypto` | 70 | 4 | 10 | 56 |
| `hutool-db` | 107 | 0 | 107 | 0 |
| `hutool-dfa` | 6 | 3 | 0 | 3 |
| `hutool-extra` | 179 | 0 | 179 | 0 |
| `hutool-http` | 72 | 0 | 72 | 0 |
| `hutool-json` | 33 | 0 | 2 | 31 |
| `hutool-jwt` | 17 | 0 | 2 | 15 |
| `hutool-log` | 46 | 6 | 33 | 7 |
| `hutool-poi` | 78 | 0 | 78 | 0 |
| `hutool-script` | 5 | 0 | 5 | 0 |
| `hutool-setting` | 16 | 3 | 0 | 13 |
| `hutool-socket` | 24 | 0 | 24 | 0 |
| `hutool-system` | 16 | 0 | 16 | 0 |
| **总计** | **1557** | **293** | **919** | **345** |

## 明确不做的模块

| 模块 | 总数 | 排除理由 |
|---|---:|---|
| `hutool-ai` | 58 | Talks to remote model endpoints through java.net sockets and needs per-vendor credentials; a KMP port belongs in a separate HTTP-based module. |
| `hutool-all` | 2 | Aggregator POM: contains only two dependency-descriptor classes, no logic to port. |
| `hutool-aop` | 15 | JDK dynamic proxies and bytecode libraries (Javassist/CGLib); there is no equivalent interception mechanism in commonMain. |
| `hutool-captcha` | 13 | Renders images with java.awt.Graphics2D; commonMain has no 2D graphics backend. |
| `hutool-db` | 107 | Built on JDBC (java.sql.Connection/ResultSet); KMP persistence has to choose a driver per platform instead. |
| `hutool-extra` | 179 | Servlet/FTP/mail/QR-code/template wrappers around javax.* and JVM-only libraries. |
| `hutool-http` | 72 | java.net.HttpURLConnection and javax.net.ssl; the multiplatform equivalent is an HTTP client such as Ktor, which users add themselves. |
| `hutool-poi` | 78 | Apache POI reads and writes Office files through java.io and java.awt; JVM-only. |
| `hutool-script` | 5 | javax.script engines (GraalJS/Nashorn) are JVM-only. |
| `hutool-socket` | 24 | java.net sockets plus NIO channels; no socket API in commonMain. |
| `hutool-system` | 16 | java.lang.management, OSHI and JVM process introspection; per-OS data must come from expect/actual code. |

完整清单（每个类、每条排除理由）：[docs/development/coverage.md](https://github.com/shijianjs/kmp-hutool/blob/main/docs/development/coverage.md)
