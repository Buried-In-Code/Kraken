import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
    `maven-publish`
}

println("Kotlin v${KotlinVersion.CURRENT}")
println("Java v${System.getProperty("java.version")}")
println("Arch: ${System.getProperty("os.arch")}")

group = "github.buriedincode"
version = "0.2.2"

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
        create<MavenPublication>("kalibak") {
            from(components["java"])
        }
    }
}
