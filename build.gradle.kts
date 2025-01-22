import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardOpenOption

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
    `maven-publish`
}

println("Kotlin v${KotlinVersion.CURRENT}")
println("Java v${System.getProperty("java.version")}")
println("Arch: ${System.getProperty("os.arch")}")

group = "github.buriedincode"
version = "0.2.3"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.kotlin.logging)
    runtimeOnly(libs.sqlite.jdbc)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.kotlin.reflect)
    testRuntimeOnly(libs.log4j2.slf4j2.impl)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    jvmToolchain(17)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version = "1.5.0"
}

tasks.test {
    environment("METRON__USERNAME", System.getenv("METRON__USERNAME"))
    environment("METRON__PASSWORD", System.getenv("METRON__PASSWORD"))
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = "current"
    resolutionStrategy {
        componentSelection {
            all {
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("kraken") {
            from(components["java"])
        }
    }
}

tasks.register("processReadme") {
    group = "documentation"
    description = "Processes the README.md file to inline SVG badges."

    doLast {
        val linkedBadgePattern = """\[\!\[(.*?)\]\((.*?)\)\]\((.*?)\)""".toRegex() // [![alt](url)](link)
        val badgePattern = """\!\[(.*?)\]\((.*?)\)""".toRegex() // ![alt](url)

        val inputPath = project.rootDir.toPath().resolve("README.md")
        val outputPath = project.buildDir.toPath().resolve("Processed-README.md")

        if (!Files.exists(inputPath)) {
            throw IllegalStateException("${inputPath.toAbsolutePath()} not found.")
        }

        var content = Files.readAllLines(inputPath).joinToString("\n")
        content = content.replaceFirst("# Kraken", "# Module Kraken")

        fun fetchSvg(url: String): String? {
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == 200 && connection.contentType.contains("image/svg+xml")) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    println("Warning: $url is not an SVG badge")
                    null
                }
            } catch (e: Exception) {
                println("Error fetching $url: ${e.message}")
                null
            }
        }

        fun processContent(pattern: Regex, replaceFunction: (MatchResult) -> String): String {
            return pattern.replace(content) { match ->
                replaceFunction(match)
            }
        }

        content = processContent(linkedBadgePattern) { match ->
            val altText = match.groupValues[1]
            val badgeUrl = match.groupValues[2]
            val linkUrl = match.groupValues[3]
            val svgContent = fetchSvg(badgeUrl)
            if (svgContent != null) {
                """<a href="$linkUrl" target="_blank">$svgContent</a>"""
            } else {
                """<a href="$linkUrl" target="_blank"><img alt="$altText" src="$badgeUrl" /></a>"""
            }
        }

        content = processContent(badgePattern) { match ->
            val altText = match.groupValues[1]
            val badgeUrl = match.groupValues[2]
            val svgContent = fetchSvg(badgeUrl)
            svgContent ?: """<img alt="$altText" src="$badgeUrl" />"""
        }

        Files.createDirectories(outputPath.parent)
        Files.writeString(outputPath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
        println("Processing complete. Output written to ${outputPath.toAbsolutePath()}")
    }
}

tasks.dokkaHtml {
    dependsOn("processReadme")
    dokkaSourceSets {
        configureEach {
            includes.from(project.buildDir.toPath().resolve("Processed-README.md"))
        }
    }
}
