plugins {
    kotlin("multiplatform")
}

// The brand image lives with the site's static files and is copied in here, so the demo and the
// documentation cannot drift apart. `srcDir(task)` only contributes the directory, not the
// dependency, hence the explicit wiring below.
val brandAssets = tasks.register<Copy>("brandAssets") {
    from(rootProject.file("docs/src/jsMain/resources/static/img")) {
        // Keep the same `img/` layout the site uses, so both pages reference `img/<logo>.png`.
        into("img")
    }
    into(layout.buildDirectory.dir("generated/brand"))
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
        jsMain {
            resources.srcDir(brandAssets)
        }

        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
            implementation(project(":kmp-hutool-core"))
        }
    }
}
