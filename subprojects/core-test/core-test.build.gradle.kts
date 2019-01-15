import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(PluginsId.kotlinMultiPlatformJvm)
}

dependencies {
    expectedBy(project(":core-common"))
    implementation(Libs.kotlinStdlibJvm)
    implementation(Libs.coroutinesCoreJvm)
    junit5TestDependencies()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}
