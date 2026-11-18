plugins {
    kotlin("multiplatform")
    id("io.github.shijianjs.ktjs.docusaurus")
}

/**
 * `docs` is the documentation site: Markdown lives in `src/jsMain/resources`, configuration is
 * Kotlin, and `./gradlew :docs:docusaurusBuild` turns both into the static site GitHub Pages serves.
 *
 * The module is loaded by Node (Docusaurus imports `docusaurus.config.mjs` from its bundle), so it
 * must not touch `window` while the module initialises.
 */
kotlin {
    js {
        useEsModules()
        nodejs()
        binaries.library()
    }

    sourceSets {
        jsMain.dependencies {
            // The wrapper brings its own `@docusaurus/*` npm dependencies: the klib records the
            // versions it was built against and Kotlin/JS installs them in the npm workspace.
            implementation(
                "io.github.shijianjs:ktjs-docusaurus:${providers.gradleProperty("ktjs-docusaurus.version").get()}",
            )
        }
    }
}

docusaurus {
    // Generates `sidebars.mjs` next to the site configuration from the exported `sidebars` function.
    sidebarsFunction = "sidebars"

    // Dev 默认中文启动：docusaurusStart 不带参数也能起中文站点；构建不受影响。
    // 单次运行仍可用 --locale en / -Pdocusaurus.locale=en 覆盖。
    startArguments = listOf("--locale", "zh-Hans")
}
