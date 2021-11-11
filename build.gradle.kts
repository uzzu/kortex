buildscript {
    dependencies {
        classpath(legacyPluginDeps.kotlin)
        classpath(legacyPluginDeps.android)
    }
}

plugins {
    base
    alias(pluginDeps.plugins.dotenv)
    alias(pluginDeps.plugins.dokka) apply false
    alias(pluginDeps.plugins.ktlint)
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
