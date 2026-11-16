# TODO

## 任务：把 Hutool 复刻为 Kotlin Multiplatform 库

在 `commonMain` 里尽可能多地实现 Hutool 的可移植功能，Kotlin 化改写而非照抄；提供中英双语文档站（为主）、可跳转的 demo 与 API 文档（GitHub Pages）；输出功能清单与自动计算的覆盖率（含未覆盖理由）；注明本项目 MIT 与上游 Mulan PSL v2 许可。

- 总计：1557
- 已完成：394
- 明确不做：1163
- 待完成：0

详细清单：[coverage.md](docs/development/coverage.md)（由 `scripts/hutool_coverage.py` 生成，不在本文件复制明细）。

### 当前待办

- [x] `hutool-json` XML 方向（`JSONXMLParser`、`JSONXMLSerializer`、`XML`、`XMLTokener`）已随 JSON 批次完成
- [x] `hutool-core`：`CityHash`（对照上游 Java 输出逐长度校验）、`RegexPool`/`PatternPool`（常量表 + 缓存）
- [x] `hutool-crypto`：`SignUtil` 的参数排序摘要签名（MD5/SHA-1/SHA-256 纯 Kotlin）
- [x] `hutool-setting` 3 个包装类与 `Argon2` 已按依赖缺失记为排除（见 `scripts/hutool-exclusions.json`）
- [ ] 每批新增类后跑 `:kmp-hutool-core:jvmTest`、`:kmp-hutool-core:jsTest`、`:kmp-hutool-core:wasmJsTest`，并刷新覆盖率
- [ ] 重建发布流水线：`release.yml` 与 `hub-release.py` 已删除（旧的按模块 `-kt.N` 模型与单版本不符），需要按当前单版本模型重新设计
- [ ] 首次发布前补全 POM 描述与仓库 Secrets（签名密钥）
- [ ] 文档站内容随覆盖率增长补充示例（cache/date/cron 已补，剩余随 json/crypto/setting 推进）

### 已完成的关键批次

- core 基础、text/convert/io/map/collection/lang、DFA、Bloom、中文日期、JWT 契约、JSON 格式化
- date：`TimeZoneUtil`、`ZoneUtil`、`DateBasic`（`kotlinx-datetime`，`localeTag` 代替 `Locale`）
- cache：`Cache`/`CacheObj`/`AbstractCache`/FIFO/LFU/LRU/NoCache/`CacheUtil`，单线程语义已写入 KDoc
- cron：支撑层 `Part`/`CronPattern`/`PatternMatcher` 与 12 个任务/解析/匹配类，5/6/7 段表达式全部可解析匹配
- json：`JSON`/`JSONObject`/`JSONArray`/`JSONNull`/`JSONTokener`/`JSONParser`/`JSONObjectIter`/`JSONSupport` 与四个序列化接口，动态模型底层用 `kotlinx-serialization-json`
- json XML：`XML`/`XMLTokener`/`JSONXMLParser`/`JSONXMLSerializer`/`ParseConfig`/`InternalJSONUtil`，XML ↔ JSON 双向转换已可用
- jwt：`Claims`/`RegisteredPayload`/`JWTHeader`/`JWTPayload`/`JWT`/`JWTValidator`/`JWTUtil`，签名仍由 `JWTSigner` 提供
- crypto：纯 Kotlin `SM3`、`HOTP`、`TOTP`（RFC 4226/6238，内置 HMAC-SHA1，已用官方测试向量验证）、`SignUtil` 参数签名（MD5/SHA-1/SHA-256）
- core.lang：`RegexPool`/`PatternPool`、`CityHash`（32/64/128 位，输出与上游 Java 8 一致）
- 依赖：core 现依赖 `kotlinx-datetime` 与 `kotlinx-serialization-json`，均支持 jvm/js/wasmJs/native
- 排除项：log、JVM 强相关 core、`WeakCache` 等 20 类已写入 `scripts/hutool-exclusions.json` 并逐条给出理由

### 限制与约束

- `commonMain` 不得引入 JVM-only API；每次改完必须过一遍 `compileKotlinJs`。
- 非核心能力（JSON、HTTP、加密、数据库、POI……）不打包进核心，需要时由使用方自行添加 KMP 依赖。
- 本地镜像只写 `~/.gradle/init.d` 与 `~/.npmrc`，仓库配置只指向公共源。
- 排除项必须有具体理由；「还没做」不能记为排除。
