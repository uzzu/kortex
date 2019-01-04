@Suppress("unused")
object AndroidSdk {
    const val compileSdkVersion = 28
    const val minSdkVersion = 16
    const val targetSdkVersion = 28
}

@Suppress("unused")
object Versions {
    // gradle plugins
    const val ktlintPlugin = "6.3.1"
    const val buildTimeTrackerPlugin = "0.11.1"
    const val nodeGradlePlugin = "1.2.0"
    const val androidGradlePlugin = "3.2.1"
    const val androidJunit5Plugin = "1.3.1.1"

    // Kotlin
    const val kotlin = "1.3.11"
    const val kotlinCoroutines = "1.1.0"

    // node
    const val node = "8.9.3"
    const val mocha = "4.1.0"
    const val mochaJunitReporter = "1.18.0"

    // Android
    const val androidxAppCompat = "1.0.2"

    // ExampleLibs
    const val timber = "4.7.1"

    // ExampleLibs for debug
    const val hyperion = "0.9.24"
    const val googleAutoService = "1.0-rc4"

    // TestLibs for JUnit
    const val junit5 = "5.3.2"
    const val junit5PlatformRunner = "1.3.1"
    const val androidJunit5InstrumentationTest = "0.2.2"
    const val assertk = "0.12"
    const val mockk = "1.8.13.kotlin13"

    // TestLibs for Android
    const val truth = "0.42"
    const val androidTestCore = "1.0.0"
    const val androidTestRunner = "1.1.0"
    const val androidTestInstrumentation = "2.0.0"
    const val androidTestExt = "1.0.0"
    const val espresso = "3.1.0"
}
