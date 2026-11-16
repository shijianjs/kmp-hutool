# TODO

## 任务：把 Hutool 复刻为 Kotlin Multiplatform 库

在 `commonMain` 里尽可能多地实现 Hutool 的可移植功能，Kotlin 化改写而非照抄；提供中英双语文档站（为主）、可跳转的 demo 与 API 文档（GitHub Pages）；输出功能清单与自动计算的覆盖率（含未覆盖理由）；注明本项目 MIT 与上游 Mulan PSL v2 许可。

- 总计：1557
- 已完成：260
- 明确不做：919
- 待完成：378

详细清单：[coverage.md](docs/development/coverage.md)（由 `scripts/hutool_coverage.py` 生成，不在本文件复制明细）。

### 当前待办

- [ ] 继续补 `hutool-core` 剩余可移植类（按包分批：`date`、`map`、`collection`、`text`、`util`、`comparator`、`codec`）
- [ ] 每批新增类后跑 `:kmp-hutool-core:jvmTest` 与 `compileKotlinJs`，并刷新覆盖率
- [ ] 补充 `date` 区域：`DateUtil` 可移植部分（格式化、解析、偏移）以 `kotlinx-datetime` 扩展函数实现
- [ ] 重建发布流水线：`release.yml` 与 `hub-release.py` 已删除（旧的按模块 `-kt.N` 模型与单版本不符），需要按当前单版本模型重新设计
- [ ] 首次发布前补全 POM 描述与仓库 Secrets（签名密钥）
- [ ] 文档站内容随覆盖率增长补充示例（当前示例集中在 core 的已移植类）

### 限制与约束

- `commonMain` 不得引入 JVM-only API；每次改完必须过一遍 `compileKotlinJs`。
- 非核心能力（JSON、HTTP、加密、数据库、POI……）不打包进核心，需要时由使用方自行添加 KMP 依赖。
- 本地镜像只写 `~/.gradle/init.d` 与 `~/.npmrc`，仓库配置只指向公共源。
- 排除项必须有具体理由；「还没做」不能记为排除。
