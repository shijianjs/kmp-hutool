import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.konan.target.HostManager

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

    // Every first-class Kotlin/Native target. klib compilation is host-independent; only linking a
    // test executable or a framework needs the host toolchain, so non-host test tasks are disabled
    // below rather than declared away: a Windows checkout still proves the code compiles for Linux,
    // Apple and Android, and CI on each OS runs the tests for its own target.
    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // kotlinx-datetime is the portable replacement for java.util.Date in ports of
            // Hutool's date utilities; it is a dependency of the library, not of consumers' optionals.
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

/**
 * Native targets whose tests this host can actually run.
 *
 * Declaring every target keeps a Windows checkout honest about Linux, Android and Apple compilation,
 * but a test executable can only be linked and executed for the host's own family: `linuxX64Test` on
 * Windows fails inside the linker instead of failing a test. CI runs each OS's own suite, so together
 * the runs cover the matrix.
 */
val runnableNativeTargets: Set<String> = when {
    HostManager.hostIsMingw -> setOf("mingwX64")
    HostManager.hostIsLinux -> setOf("linuxX64")
    HostManager.hostIsMac -> {
        // Intel and Apple silicon Macs can each run only their own tests: the simulator binaries are
        // arch-specific, and an iOS device target has no test runner at all.
        val armHost = System.getProperty("os.arch").contains("aarch64") ||
            System.getProperty("os.arch").contains("arm64")
        if (armHost) setOf("macosArm64", "iosSimulatorArm64") else setOf("macosX64", "iosX64")
    }
    else -> emptySet()
}

tasks.withType<KotlinNativeTest>().configureEach {
    if (name.removeSuffix("Test") !in runnableNativeTargets) {
        enabled = false
    }
}
