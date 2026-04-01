import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.7.2"
}

group = "com.shadowsurf"
version = "0.1.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        testFramework(TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    pluginConfiguration {
        name = "ShadowSurf"
        version = project.version.toString()
        description = "A lightweight in-IDE browser tool window with theme-aware dark page enhancement."

        ideaVersion {
            sinceBuild = "243"
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
