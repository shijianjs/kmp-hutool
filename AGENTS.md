# AGENTS.md

本文件说明 `kmp-hutool` 的长期约定。项目目标是把 Hutool 的通用工具改写为 Kotlin Multiplatform 库，文档站是主要交付物。

## 项目结构

- `kmp-hutool-core`：唯一发布模块，`commonMain` 实现，target 为 `jvm`、`js`、`wasmJs` 以及全套 Kotlin/Native（`linuxX64`、`linuxArm64`、`mingwX64`、`macosX64`、`macosArm64`、`iosX64`、`iosArm64`、`iosSimulatorArm64`）。版本 `kmp-hutool.version`（当前 `5.8.48-kt.1`），group `io.github.shijianjs`。
  - 所有 target 都声明，保证任意宿主都能编译全部 klib；非宿主的 `*Test` 在构建脚本里禁用，由 CI 的 Linux / Windows / macOS 三个任务各跑自己的原生套件。加 target 时同步改 `runnableNativeTargets` 与 `.github/workflows/ci.yml`。
- `demo`：演示站，外壳（`vaadin-app-layout` 外壳、抽屉菜单、语言与主题切换、路由、页面框架）全部来自
  `ktjs-demo-kit`，本仓只写内容：一页一族工具。发布在 Pages 的 `/demo/`。
  - 每页是「一组调用 + 结果」的卡片网格：`page/ResultPage.kt` 是这套页面的公共落点，具体页面只提供
    `ResultRow`（调用表达式与返回值，二者都不翻译）和两种语言的标题/提示。
  - 返回值在页面构造时算一次就固定下来：它们都是确定值，切换语言只改标题，不重算也不会跳动。
  - 页面按业务分包（`page/text`、`page/number`、`page/collection`、`page/encoding`、`page/dates`、
    `page/china`、`page/hashes`），不要为了「整齐」新建 `components/`、`utils/` 这类技术目录。
  - `src/jsMain/resources` 里没有 `demo.css`：Lumo 由 `copyLumoStyles` 从 npm 工作区拷入，其余样式由 kit 自己的
    sheet 与 Vaadin 组件承担；页面里出现多行 CSS 去拼卡片/行/徽章，说明把组件语义写成了样式。
- `docs`：文档站，Kotlin/JS 模块，配置在 `docs/src/jsMain/kotlin/.../Config.kt`，页面在 `docs/src/jsMain/resources/docs/`，译文在 `docs/src/jsMain/resources/i18n/zh-Hans/`。英文是源语言。
  落地页 `resources/pages/index.mdx` 用 `ktjs-docusaurus-kit` 的 `<ktjs-hero>` / `<ktjs-features>` / `<ktjs-next-steps>`；
  这些元素和右侧目录折叠 `TocToggle` 的浏览器 bundle 由 `kitBundle` + `extractKitBundle` 拷到 `static/ktjs-client/`，
  再由 `ClientModule.kt` 在 hydration 之后加载（`Config.kt` 的 `clientModules` 列出生成出来的 `./client-module.mjs`）。
- `scripts/hutool_inventory.py`、`scripts/hutool_coverage.py`、`scripts/hutool-exclusions.json`：功能清单与覆盖率。
- `docs/development/coverage.md`：脚本生成的完整清单（不要在文档站手工复制数字）。

## 构建与测试

```powershell
.\gradlew.bat :kmp-hutool-core:jvmTest --console=plain   # 主测试入口
.\gradlew.bat :kmp-hutool-core:jsTest --console=plain    # 同一套测试在 Node 上跑
.\gradlew.bat :kmp-hutool-core:allTests --console=plain  # 当前宿主能跑的全部（含本地原生套件）
.\gradlew.bat :demo:jsBrowserDistribution
.\gradlew.bat :docs:docusaurusBuild
.\gradlew.bat pagesSite
python scripts\hutool_coverage.py                        # 刷新覆盖率与站点页面
```

- `build` 会编译全部 target 的 klib（含所有 Native 目标），因此本地能提前发现平台专属 API；但原生测试二进制只能在同族宿主运行，非宿主测试已自动禁用，不要为了“全跑”去掉这个开关。

- `commonMain` 只能用跨平台 API。JVM 编译通过不代表 JS/Wasm 通过：`forEachRemaining`、`sortedMapOf`、`removeIf`、`StringBuilder.delete/setCharAt`、`@Synchronized` 都只在 JVM 上存在，改完必须跑一次 `compileKotlinJs`。
- Kotlin `object` 内的成员会遮蔽 stdlib 同名函数：`NumberUtil.ceil/floor` 必须写 `kotlin.math.ceil/floor`，否则无限递归（StackOverflow）。
- `vararg` 转发必须写 `*args`。
- `Array<T>.copyOf(n)` 返回 `Array<T?>`，需要 `@Suppress("UNCHECKED_CAST")` 强转，或改用 list。
- 十六进制 Long 字面量不能越过 `0x7FFF_FFFF_FFFF_FFFF`；UUID/位掩码用 `and`/`or` 组合。

## 移植约定

- 每个移植类在 KDoc 写 `@hutool <上游全限定类名>`；合并门面写两个标签（`StrUtil` 同时覆盖 `CharSequenceUtil`，`CollUtil` 同时覆盖 `CollectionUtil`）。覆盖率脚本只认这个标签。
- 包名镜像上游：`io.github.shijianjs.kmphutool.core.<上游子包>`。
- Kotlin 化改写优先：合并门面、`String` 替代 `CharSequence` 返回、`data class` 值对象、`inline reified` 替代 `Class` 参数、显式 `toXxx` 替代反射。
- 不移植：反射（`BeanUtil`/`ClassUtil`/注解）、`java.io`/`java.nio`、线程与定时器、AWT/Swing、`javax.tools`、`BigDecimal`、`java.util.stream`、GBK。放弃的类必须写进 `scripts/hutool-exclusions.json` 并给出具体理由。
- 日期统一用 `kotlinx-datetime`（唯一依赖），不要引入格式化库来复刻 `DateUtil`。
- 上游行为以本地克隆 `S:\workspace\github\chinabugotech\hutool` 为准；不要抓 GitHub 网页（国内常超时）。

## 覆盖率与清单

- `hutool_inventory.py` 输出 `build/hutool-inventory.json`：1557 个上游 Java 类，每个一项。
- `hutool_coverage.py` 扫描 `*/src/commonMain/kotlin` 的 `@hutool` 标签，套用排除规则（整模块 + 单类 + "husk" JDK-only 规则），生成 `docs/development/coverage.md`、`build/hutool-coverage.json`，以及文档站的 `guide/coverage.md` 中英文两份。
- 统计恒等式：总数 = 已完成 + 明确不做 + 待完成。不要把「暂时没做」记为排除。

## 文档与演示

- 新增/修改页面时同时更新 `i18n/zh-Hans/...` 对应文件；`docs` 的 `onBrokenLinks = throw`，死链会直接让构建失败。
- 文档站、demo、API 的链接由 `Config.kt` 顶部的 `SITE_URL`/`SITE_BASE_URL`/`DEMO_URL`/`API_URL` 决定，改 Pages 布局要一起改。
- demo 只用公共 API，不引入 UI 框架；DOM 用 `document.createElement` 构造，禁止拼 HTML 字符串。
- 许可证：本项目 MIT，上游 Mulan PSL v2。`NOTICE` 与 `guide/licensing` 必须同时保留两份声明。

## 本地镜像

- Maven 镜像只放 `~/.gradle/init.d/*.gradle`（Groovy，不用 kts），npm 镜像只放 `~/.npmrc`；仓库里的 `settings.gradle.kts` 与 `package.json` 必须指向公共源，CI 才能复现。
- 刷新锁文件前先 `$env:npm_config_registry='https://registry.npmjs.org/'`，否则镜像地址会写进 `kotlin-js-store/package-lock.json`，CI 报 `Lock file was changed`。

## Git

- 提交信息用英文，一行摘要 + 可选正文；不要 `--amend` 已推送的提交。完成后自动 `commit` + `push`（除非用户另有指示）。
