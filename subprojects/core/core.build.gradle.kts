import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    id(PluginsId.kotlinMultiPlatformCommon)
}

dependencies {
    implementation(Libs.kotlinStdlibCommon)
    implementation(Libs.coroutinesCoreCommon)
    multiPlatformCommonTestDependencies()
}

tasks.withType<KotlinCompile<*>> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}
