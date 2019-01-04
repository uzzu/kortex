import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

@Suppress("unused")
object PluginsId {
    // Kotlin MPP
    const val kotlinMultiPlatformCommon = "org.jetbrains.kotlin.platform.common"
    const val kotlinMultiPlatformJvm = "org.jetbrains.kotlin.platform.jvm"

    // Js
    const val nodeGradle = "com.moowork.node"

    // Android
    const val androidApplication = "com.android.application"
    const val androidLibrary = "com.android.library"
    const val androidJunit5 = "de.mannodermaus.android-junit5"

    // Android Kotlin
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"

    // misc
    const val ktlint = "org.jlleitschuh.gradle.ktlint"
    const val buildTimeTracker = "net.rdrei.android.buildtimetracker"
}

object PluginClasspath {
    const val kotlin = PluginModules.kotlin
}

private object PluginModules {
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlintPlugin}"
    const val buildTimeTracker = "net.rdrei.android.buildtimetracker:gradle-plugin:${Versions.buildTimeTrackerPlugin}"
}

fun Settings.applyPluginManagement() {
    pluginManagement {
        repositories {
            google()
            mavenCentral()
            maven(url = "https://plugins.gradle.org/m2/")
            jcenter()
        }

        resolutionStrategy {
            eachPlugin {
                when (requested.id.id) {
                    PluginsId.kotlinMultiPlatformCommon,
                    PluginsId.kotlinMultiPlatformJvm -> {
                        useModule(PluginModules.kotlin)
                    }
                    PluginsId.ktlint -> {
                        useModule(PluginModules.ktlint)
                    }
                    PluginsId.buildTimeTracker -> {
                        useModule(PluginModules.buildTimeTracker)
                    }
                }
            }
        }
    }
}
