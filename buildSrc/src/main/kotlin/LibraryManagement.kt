import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

fun Project.allProjectsRepositories() {
    allprojects {
        repositories {
            google()
            mavenCentral()
            maven(url = "https://jitpack.io")
            jcenter()
        }
    }
}

@Suppress("unused")
object Libs {
    const val kotlinStdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
    const val kotlinStdlibJvm = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinStdlibJs = "org.jetbrains.kotlin:kotlin-stdlib-js:${Versions.kotlin}"
    const val coroutinesCoreCommon = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}"
    const val coroutinesCoreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
    const val coroutinesCoreNative = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    const val coroutinesCoreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
}

@Suppress("unused")
object ExampleLibs {
}

@Suppress("unused")
object TestLibs {
    // kotlin
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    // assertion
    const val assertkCommon = "com.willowtreeapps.assertk:assertk:${Versions.assertk}"
    const val assertkJvm = "com.willowtreeapps.assertk:assertk-jvm:${Versions.assertk}"

    // mock
    const val mockkCommon = "io.mockk:mockk-common:${Versions.mockk}"
    const val mockkJvm = "io.mockk:mockk:${Versions.mockk}"

    // kotlin-multiplatform-common specific
    const val kotlinTestCommon = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
    const val kotlinTestAnnotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"

    // junit5 test
    const val kotlinTestJunit5 = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlin}"
    const val junit5 = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
    const val junit5Param = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
}

// region helpers

private const val implementation = "implementation"
private const val testImplementation = "testImplementation"
private const val testRuntimeOnly = "testRuntimeOnly"
private const val androidTestImplementation = "androidTestImplementation"
private const val androidTestRuntimeOnly = "androidTestRuntimeOnly"

fun DependencyHandlerScope.multiPlatformCommonTestDependencies() {
    testImplementation(TestLibs.kotlinTestCommon)
    testImplementation(TestLibs.kotlinTestAnnotationsCommon)
    testImplementation(TestLibs.coroutinesTest)
    testImplementation(TestLibs.assertkCommon) {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(TestLibs.mockkCommon) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

fun DependencyHandlerScope.junit5TestDependencies() {
    testImplementation(TestLibs.kotlinReflect)
    testImplementation(TestLibs.kotlinTestJunit5) {
        exclude(group = "org.junit.jupiter")
    }
    testImplementation(TestLibs.junit5)
    testImplementation(TestLibs.junit5Param)
    testRuntimeOnly(TestLibs.junit5Engine)
    testImplementation(TestLibs.coroutinesTest)
    testImplementation(TestLibs.assertkJvm) {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation(TestLibs.mockkJvm) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

// endregion
