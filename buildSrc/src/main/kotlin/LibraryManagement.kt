import org.gradle.api.Project
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
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
}

@Suppress("unused")
object ExampleLibs {
}

object TestLibs {
    // kotlin
    const val kotlinReflectJvm = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    // assertion
    const val assertk = "com.willowtreeapps.assertk:assertk:${Versions.assertk}"

    // kotlin-multiplatform-common specific
    const val kotlinTestCommon = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
    const val kotlinTestAnnotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"

    // junit5 test
    const val kotlinTestJunit5 = "org.jetbrains.kotlin:kotlin-test-junit5:${Versions.kotlin}"
    const val junit5 = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
    const val junit5Engine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
    const val junit5Param = "org.junit.jupiter:junit-jupiter-params:${Versions.junit5}"
}
