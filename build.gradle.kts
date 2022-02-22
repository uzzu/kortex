buildscript {
    dependencies {
        classpath(legacyPluginLibs.kotlin)
        classpath(legacyPluginLibs.android)
    }
}

plugins {
    base
    alias(pluginLibs.plugins.dotenv)
    alias(pluginLibs.plugins.dokka) apply false
    alias(pluginLibs.plugins.ktlint)
}

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
