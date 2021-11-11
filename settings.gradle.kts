@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")

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
        val kotlin = "1.5.30"
        val kotlinxCoroutines = "1.5.2"
        val junit5 = "5.6.2"
        val assertk = "0.25"
        val androidGradlePlugin = "7.0.3" // sdk version is defined in bulidSrc
        val dotenv = "1.2.0"
        val ktlint = "10.2.0"

        create("legacyPluginDeps") {
            alias("kotlin").to("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
            alias("android").to("com.android.tools.build:gradle:$androidGradlePlugin")
        }

        create("pluginDeps") {
            alias("dotenv").toPluginId("co.uzzu.dotenv.gradle").version(dotenv)
            alias("kotlin-multiplatform").toPluginId("org.jetbrains.kotlin.multiplatform").version(kotlin)
            alias("android-library").toPluginId("com.android.library").version(androidGradlePlugin)
            alias("ktlint").toPluginId("org.jlleitschuh.gradle.ktlint").version(ktlint)
            alias("dokka").toPluginId("org.jetbrains.dokka").version(kotlin)
        }

        create("deps") {
            alias("kotlinx-coroutines-core").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutines")
        }

        create("testDeps") {
            alias("kotlin-test-common").to("org.jetbrains.kotlin:kotlin-test-common:$kotlin")
            alias("kotlin-test-annotation-common").to("org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlin")
            alias("kotlin-test-junit5").to("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin")
            alias("junit5-api").to("org.junit.jupiter:junit-jupiter-api:$junit5")
            alias("junit5-engine").to("org.junit.jupiter:junit-jupiter-engine:$junit5")
            alias("junit5-params").to("org.junit.jupiter:junit-jupiter-params:$junit5")
            alias("kotlin-reflect-jvm").to("org.jetbrains.kotlin:kotlin-reflect:$kotlin")
            alias("assertk").to("com.willowtreeapps.assertk:assertk:$assertk")
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
