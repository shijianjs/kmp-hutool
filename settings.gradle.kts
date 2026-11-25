pluginManagement {
    repositories {
        // The documentation site is built with the ktjs-docusaurus Gradle plugin: released versions
        // come from Central, `mavenLocal` is what serves a SNAPSHOT one while the site is developed
        // against an unreleased plugin. An empty local repository falls through to the next
        // repository, so the order costs nothing in CI.
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }

    // The plugin's version is a catalog entry (`libs.plugins.ktjs.docusaurus` in
    // `gradle/libs.versions.toml`); `docs` applies it with `alias(...)`, so no resolution strategy
    // is needed here.
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        // Gradle does not fall back to the next repository once metadata is found, so pin the
        // Kotlin libraries to Central: a local mirror may serve metadata without the klibs, and a
        // POM-only `kotlin-stdlib` in the local repository has no JS variant at all.
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                includeGroup("org.jetbrains.kotlin-wrappers")
                includeGroup("org.jetbrains.kotlinx")
                includeGroup("org.jetbrains.kotlin")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "kmp-hutool"

// Every module keeps Hutool's own module boundaries, so a consumer can depend on the part it needs:
// the core utilities are one artifact, the optional packages (json, crypto, ...) are separate ones.
include(":kmp-hutool-core")

// The demo page, published under /demo/ of the Pages site. Not a published module.
include(":demo")

// The documentation site: Kotlin configuration plus Markdown, assembled by the ktjs-docusaurus
// Gradle plugin into a static site that `pagesSite` publishes at the Pages root.
include(":docs")
