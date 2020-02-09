import com.google.common.base.CaseFormat
import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlinMultiPlatform
    bintray
    `maven-publish`
}

kotlin {
    jvm()
    js {
        browser()
        nodejs()
        compilations.all {
            tasks.withType<Kotlin2JsCompile> {
                kotlinOptions {
                    moduleKind = "umd"
                    sourceMap = true
                    metaInfo = true
                }
            }
        }
    }

    linuxX64()
    iosArm64()
    iosX64()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libs.kotlinStdlibCommon)
                implementation(Libs.coroutinesCoreCommon)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(TestLibs.kotlinTestCommon)
                implementation(TestLibs.kotlinTestAnnotationsCommon)
                implementation(TestLibs.assertk)
                implementation(project(":testing"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Libs.kotlinStdlibJvm)
                implementation(Libs.coroutinesCoreJvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                runtimeOnly(TestLibs.junit5Engine)
                implementation(TestLibs.kotlinTestJunit5)
                implementation(TestLibs.kotlinReflectJvm)
                implementation(TestLibs.junit5)
                implementation(TestLibs.junit5Param)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(Libs.kotlinStdlibJs)
                implementation(Libs.coroutinesCoreJs)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(TestLibs.kotlinTestJs)
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Libs.coroutinesCoreNative)
            }
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        val linuxX64Main by getting
        val linuxX64Test by getting
        val iosArm64Main by getting
        val iosArm64Test by getting
        val iosX64Main by getting
        val iosX64Test by getting
        val macosX64Main by getting
        val macosX64Test by getting
        configure(listOf(linuxX64Main, iosArm64Main, iosX64Main, macosX64Main)) {
            dependsOn(nativeMain)
        }
        configure(listOf(linuxX64Test, iosArm64Test, iosX64Test, macosX64Test)) {
            dependsOn(nativeTest)
        }
    }
}

tasks {
    named<Test>("jvmTest") {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }

    val iosTest = register("iosTest") {
        val device = project.findProperty("iosDevice")?.toString() ?: "iPhone 8"
        val testExecutable = kotlin.targets.getByName<KotlinNativeTarget>("iosX64").binaries.getTest("DEBUG")
        dependsOn(testExecutable.linkTaskName)

        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Runs tests for target 'ios' on an iOS simulator"

        doLast {
            exec {
                println(testExecutable.outputFile.absolutePath)
                commandLine("xcrun", "simctl", "spawn", "--standalone", device, testExecutable.outputFile.absolutePath)
            }
        }
    }

    // alias to allTests task (Kotlin MPP does not have test task)
    register("test") { dependsOn("allTests") }
}

setProperty("archivesBaseName", publishingArtifactIdBase)

bintray {
    user = bintrayUser
    key = bintrayApiKey
    publish = false
    setPublications(
        *publishing.publications
            .withType<MavenPublication>()
            .map { it.name }
            .toTypedArray()
    )
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = Bintray.repo
        name = publishingArtifactIdBase
        desc = Bintray.desc
        userOrg = Bintray.userOrg
        websiteUrl = Bintray.websiteUrl
        vcsUrl = Bintray.vcsUrl
        issueTrackerUrl = Bintray.issueTrackerUrl
        githubRepo = Bintray.githubRepo
        githubReleaseNotesFile = Bintray.githubReleaseNoteFile
        setLabels(* Bintray.labels)
        setLicenses(*Bintray.licenses)
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = publishingArtifactVersion
        })
    })
}

afterEvaluate {
    publishing {
        repositories {
            maven(url = Bintray.mavenUrl)
        }

        publications.withType<MavenPublication>().all {
            val publishingArtifactId = when (name) {
                "kotlinMultiplatform" -> {
                    "$publishingArtifactIdBase"
                }
                "metadata" -> {
                    "$publishingArtifactIdBase-common"
                }
                "androidRelease" -> {
                    "$publishingArtifactIdBase-android"
                }
                else -> {
                    "$publishingArtifactIdBase-${CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name)}"
                }
            }
            groupId = publishingGroupId
            artifactId = publishingArtifactId
            version = publishingArtifactVersion
            pom {
                name.set(publishingArtifactId)
                description.set(MavenPublications.description)
                url.set(MavenPublications.url)
                licenses {
                    license {
                        name.set(MavenPublications.license)
                        url.set(MavenPublications.licenseUrl)
                        distribution.set(MavenPublications.licenseDistribution)
                    }
                }
                developers {
                    developer {
                        id.set(MavenPublications.developersId)
                        name.set(MavenPublications.developersName)
                        organization.set(MavenPublications.organization)
                        organizationUrl.set(MavenPublications.organizationUrl)
                    }
                }
                scm {
                    url.set(MavenPublications.scmUrl)
                }
            }
        }
    }
}
