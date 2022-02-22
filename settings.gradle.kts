@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        val kotlin = "1.6.10"
        val kotlinxCoroutines = "1.6.0"
        val junit5 = "5.6.2"
        val assertk = "0.25"
        val androidGradlePlugin = "7.0.3" // sdk version is defined in bulidSrc
        val dotenv = "1.2.0"
        val ktlint = "10.2.1"

        create("legacyPluginLibs") {
            library("kotlin", "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
            library("android", "com.android.tools.build:gradle:$androidGradlePlugin")
        }

        create("pluginLibs") {
            plugin("dotenv", "co.uzzu.dotenv.gradle").version(dotenv)
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").version(kotlin)
            plugin("android-library", "com.android.library").version(androidGradlePlugin)
            plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version(ktlint)
            plugin("dokka", "org.jetbrains.dokka").version(kotlin)
        }

        create("libs") {
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutines")
        }

        create("testLibs") {
            library("kotlin-test-common", "org.jetbrains.kotlin:kotlin-test-common:$kotlin")
            library("kotlin-test-annotation-common", "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlin")
            library("kotlin-test-junit5", "org.jetbrains.kotlin:kotlin-test-junit5:$kotlin")
            library("junit5-api", "org.junit.jupiter:junit-jupiter-api:$junit5")
            library("junit5-engine", "org.junit.jupiter:junit-jupiter-engine:$junit5")
            library("junit5-params", "org.junit.jupiter:junit-jupiter-params:$junit5")
            library("kotlin-reflect-jvm", "org.jetbrains.kotlin:kotlin-reflect:$kotlin")
            library("assertk", "com.willowtreeapps.assertk:assertk:$assertk")
        }
    }
}

rootProject.name = "kortex"

// region subprojects

fun Settings.includeSubProject(name: String) {
    include(":$name")
    project(":$name").projectDir = File("$rootDir/subprojects/$name")
    project(":$name").buildFileName = "$name.build.gradle.kts"
}

includeSubProject("core")

// endregion

// region examples

fun Settings.includeExampleProject(name: String, dir: String) {
    include(":examples:$name")
    project(":examples:$name").projectDir = File("$rootDir/examples/$dir")
    project(":examples:$name").buildFileName = "$name.build.gradle.kts"
}

// endregion
