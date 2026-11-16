import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

description =
    "Kotlin Multiplatform port of Hutool's core utilities: strings, collections, numbers, ids, " +
        "codecs, escapes, regex and conversion, all usable from commonMain."

kotlin {
    jvm()

    js {
        nodejs()
        useEsModules()
        binaries.library()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            // kotlinx-datetime is the portable replacement for java.util.Date in ports of
            // Hutool's date utilities; it is a dependency of the library, not of consumers' optionals.
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
