plugins {
    kotlin("multiplatform")
}

// The demo is a standalone playground page: a Kotlin/JS executable that runs the ported utilities in
// the browser, with its labels switching between English and Chinese.
kotlin {
    js {
        useEsModules()
        binaries.executable()
        browser()
    }

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
            implementation(project(":kmp-hutool-core"))
        }
    }
}
