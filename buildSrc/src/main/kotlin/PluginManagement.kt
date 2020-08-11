import org.gradle.api.plugins.ObjectConfigurationAction
import org.gradle.kotlin.dsl.ScriptHandlerScope
import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec

@Suppress("unused")
object PluginsId {
    // Js
    const val nodeGradle = "com.moowork.node"

    // Android
    const val androidJunit5 = "de.mannodermaus.android-junit5"

    // Android Kotlin
    const val kotlinAndroid = "kotlin-android"
    const val kotlinAndroidExtensions = "kotlin-android-extensions"
}

// region kotlin

fun ScriptHandlerScope.legacyBuildScriptClasspath() {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
    }
}

val PluginDependenciesSpec.kotlinMultiPlatform
    get() = id("org.jetbrains.kotlin.multiplatform") // version Versions.kotlin // for legacy

val PluginDependenciesSpec.androidLibrary
    get() = id("com.android.library")

// endregion

// region ktlint

const val ktlintPluginId = "org.jlleitschuh.gradle.ktlint"
val PluginDependenciesSpec.ktlint
    get() = id(ktlintPluginId) version Versions.ktlintPlugin
val ObjectConfigurationAction.ktlint
    get() = plugin(ktlintPluginId)

// endregion

// region general

val PluginDependenciesSpec.buildTimeTracker
    get() = id("net.rdrei.android.buildtimetracker") version Versions.buildTimeTrackerPlugin

val PluginDependenciesSpec.bintray
    get() = id("com.jfrog.bintray") version Versions.bintray

// endregion
