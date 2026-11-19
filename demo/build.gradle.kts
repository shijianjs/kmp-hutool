plugins {
    kotlin("multiplatform")
}

// The demo shell (`ktjs-demo-kit`) is built from Vaadin's published wrapper, which ships the
// JavaScript side of `@vaadin/vaadin-lumo-styles` but not the CSS: that is a page-level `<link>`,
// taken from the npm workspace the Kotlin/JS plugin installs at the root.
val lumoStyles = rootProject.layout.buildDirectory.dir("js/node_modules/@vaadin/vaadin-lumo-styles/dist")

val copyLumoStyles = tasks.register<Copy>("copyLumoStyles") {
    description = "Copies the pre-built Lumo stylesheet into the demo resources."
    dependsOn(":kotlinNpmInstall")
    from(lumoStyles) {
        include("lumo.css")
        include("utility.css")
    }
    into(layout.buildDirectory.dir("generated/lumo"))
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

// The demo is a standalone site: a Kotlin/JS executable that runs the ported utilities in the browser,
// with the page frame, the menu and the language switch belonging to `ktjs-demo-kit`.
kotlin {
    js {
        useEsModules()

        // Custom elements need a class: at the default ES5 target Kotlin emits
        // `function X() { HTMLElement.call(this) }`, and a DOM constructor may only be invoked with
        // `new` — the tag never upgrades. The demo shell is a custom element, so ES2015 is required;
        // the opt-in is the same one the demo kit compiles with, for the `JsAny` interop it exposes.
        compilerOptions {
            target = "es2015"
            optIn.add("kotlin.js.ExperimentalWasmJsInterop")
        }

        binaries.executable()
        browser()
    }

    sourceSets {
        jsMain {
            resources.srcDir(brandAssets)
            resources.srcDir(copyLumoStyles)
        }

        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
            // The ported utilities the demo demonstrates.
            implementation(project(":kmp-hutool-core"))
            // The shared demo shell: layout, menu, router, i18n. Published to `mavenLocal` by a
            // working tree of the `ktjs-dom-kit` repository until the release batch.
            implementation(
                "io.github.shijianjs:ktjs-demo-kit-js:${providers.gradleProperty("ktjs-demo-kit.version").get()}",
            )
        }
    }
}

// `srcDir(task)` contributes the directory but not the task dependency; resource processing is what
// reads that directory, so the edge belongs there.
tasks.named("jsProcessResources") {
    dependsOn(brandAssets, copyLumoStyles)
}
