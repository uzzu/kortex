buildscript {
    legacyBuildScriptClasspath()
}

plugins {
    base
    ktlint
    buildTimeTracker
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
        ktlint
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

buildtimetracker {
    reporters {
        register("summary") {
            options["ordered"] = "true"
            options["barstyle"] = "ascii"
            options["shortenTaskNames"] = "false"
        }
    }
}
