buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
    }
}

plugins {
    base
    id("co.uzzu.dotenv.gradle") version "1.1.0"
    id("org.jetbrains.dokka") version Versions.kotlin apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

allProjectsRepositories()

allprojects {
    tasks {
        withType<Test> {
            testLogging {
                showStandardStreams = true
                events("passed", "failed")
            }
        }
    }
}

subprojects {
    apply {
        plugin("org.jlleitschuh.gradle.ktlint")
    }

    ktlint {
        verbose.set(true)
        android.set(true)
        outputToConsole.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        }
        ignoreFailures.set(true)
    }
}
