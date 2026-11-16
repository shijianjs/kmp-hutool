import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.publish.PublishingExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmExtension

plugins {
    kotlin("multiplatform") version "2.4.21" apply false
    id("com.vanniktech.maven.publish") version "0.37.0" apply false
    id("org.jetbrains.dokka") version "2.3.0"
}

// Same prefix-based discovery as the other shijianjs libraries: the name decides whether a subproject
// is published, documented and counted in the version report.
val publishedPrefix = "kmp-hutool-"

// GitHub-backed namespace; verify it once in the Central Portal. Setting the root `group` alone does
// not propagate to subprojects.
val publishedGroup = "io.github.shijianjs"

// The upstream Hutool release this port follows plus this project's own revision. Modules release
// together while the API is still being filled in, so one version for the whole repository.
val baseVersion = providers.gradleProperty("kmp-hutool.version").get()

// A working tree is never a release: `-SNAPSHOT` marks a build that Maven Central does not have.
val versionSuffix =
    if (providers.gradleProperty("kmp-hutool.release").map(String::toBoolean).getOrElse(false)) "" else "-SNAPSHOT"

allprojects {
    group = publishedGroup
    version = "$baseVersion$versionSuffix"
}

// Dokka aggregates every published module into one API reference; `demo` and `docs` are not part of
// the published API surface, so the prefix filter excludes them.
dependencies {
    subprojects
        .filter { it.name.startsWith(publishedPrefix) }
        .forEach { dokka(project(it.path)) }
}

dokka {
    dokkaPublications.html {
        moduleName.set("kmp-hutool")
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}

// Keep a single webpack instance across the npm workspace: npm otherwise installs a second copy for
// source-map-loader's peer dependency, and a test bundle may fail with
// "The 'compilation' argument must be an instance of Compilation".
plugins.withType<NodeJsRootPlugin> {
    extensions.getByType(NpmExtension::class.java).override("webpack", "5.108.1")
}

subprojects {
    if (name.startsWith(publishedPrefix)) {
        apply(plugin = "org.jetbrains.dokka")
    }
}

// Publishing is configured once for every module so that coordinates, POM metadata, signing and the
// Maven Central target cannot drift apart.
//
// klibs.io discovers libraries automatically: it reads the POM (url / scm.url must point at the
// GitHub repository) and requires the kotlin-tooling-metadata.json produced by the Kotlin
// Multiplatform plugin. Keep both intact when changing this block.
subprojects {
    if (!name.startsWith(publishedPrefix)) return@subprojects

    val module = this

    apply(plugin = "com.vanniktech.maven.publish")

    // The subproject's Kotlin Multiplatform plugin is applied after this root script runs. Choosing
    // the javadoc jar earlier would hit the "no compatible plugin" branch and the plugin's
    // afterEvaluate default (Dokka HTML) would win.
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<MavenPublishBaseExtension> {
            // Maven Central requires a javadoc jar, but Dokka's HTML output duplicates its static
            // assets into every module's jar. The API reference is deployed to the Pages site, so
            // publish the required empty jar instead of the HTML copy.
            configureBasedOnAppliedPlugins(javadocJar = JavadocJar.Empty())
        }
    }

    extensions.configure<MavenPublishBaseExtension> {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()

        // Gradle writes .md5/.sha1 next to every .asc signature; Central does not need them and they
        // count toward the monthly file quota.
        excludeSignatureChecksums()

        pom {
            name.set(module.name)
            description.set(
                module.provider {
                    module.description ?: "Kotlin Multiplatform port of Hutool utilities."
                }
            )
            url.set("https://github.com/shijianjs/kmp-hutool")
            inceptionYear.set("2026")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://github.com/shijianjs/kmp-hutool/blob/main/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("shijianjs")
                    name.set("shijianjs")
                    url.set("https://github.com/shijianjs")
                }
            }

            scm {
                url.set("https://github.com/shijianjs/kmp-hutool")
                connection.set("scm:git:git://github.com/shijianjs/kmp-hutool.git")
                developerConnection.set("scm:git:ssh://git@github.com/shijianjs/kmp-hutool.git")
            }
        }
    }

    // A plain file repository produces the Maven repository layout without contacting Central.
    // Task name: publishAllPublicationsToHubStagingRepository
    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "hubStaging"
                url = module.layout.buildDirectory.dir("hub-staging").get().asFile.toURI()
            }
        }
    }
}

// Prints "<module>=<version>" for every publishable module, for release tooling.
tasks.register("printPublishedVersions") {
    val publishedModules = subprojects.filter { it.name.startsWith(publishedPrefix) }
    doLast {
        publishedModules
            .associate { it.name to it.version.toString() }
            .toSortedMap()
            .forEach { (name, version) -> println("$name=$version") }
    }
}

// Assembles the GitHub Pages site: the documentation at the root, the demo under /demo/ and the API
// reference under /api/. The docs site's `baseUrl` and the links it builds (see Config.kt) have to
// mirror this layout.
tasks.register<Sync>("pagesSite") {
    dependsOn(":demo:jsBrowserDistribution", ":docs:docusaurusBuild", "dokkaGeneratePublicationHtml")
    into(layout.buildDirectory.dir("pages"))
    from(layout.buildDirectory.dir("js/docusaurus/docs/build"))
    from(project(":demo").layout.buildDirectory.dir("dist/js/productionExecutable")) {
        into("demo")
    }
    from(layout.buildDirectory.dir("dokka/html")) {
        into("api")
    }
}
