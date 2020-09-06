import com.google.common.base.CaseFormat
import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    androidLibrary
    kotlinMultiPlatform
    bintray
    `maven-publish`
}

android {
    compileSdkVersion(AndroidSdk.compileSdkVersion)

    defaultConfig {
        minSdkVersion(AndroidSdk.minSdkVersion)
        targetSdkVersion(AndroidSdk.targetSdkVersion)
        versionName = publishingArtifactVersion
        consumerProguardFiles("proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    sourceSets {
        val main by getting {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
        }
        val test by getting {
            java.srcDirs("src/androidTest/kotlin")
        }
    }
}

kotlin {
    jvm()
    android {
        publishAllLibraryVariants()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libs.coroutinesCore)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(TestLibs.kotlinTestCommon)
                implementation(TestLibs.kotlinTestAnnotationsCommon)
                implementation(TestLibs.assertk)
            }
        }

        val jvmMain by getting {
            dependencies {
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

        val androidMain by getting {
            dependencies {
            }
        }

        val androidTest by getting {
            dependencies {
                runtimeOnly(TestLibs.junit5Engine)
                implementation(TestLibs.kotlinTestJunit5)
                implementation(TestLibs.kotlinReflectJvm)
                implementation(TestLibs.junit5)
                implementation(TestLibs.junit5Param)
            }
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
    pkg(
        delegateClosureOf<BintrayExtension.PackageConfig> {
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
            version(
                delegateClosureOf<BintrayExtension.VersionConfig> {
                    name = publishingArtifactVersion
                }
            )
        }
    )
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "bintray"
                url = uri("https://api.bintray.com/content/$bintrayUser/${Bintray.repo}/$publishingArtifactIdBase/$publishingArtifactVersion;override=1;publish=0") // ktlint-disable max-line-length
                credentials {
                    username = bintrayUser
                    password = bintrayApiKey
                }
            }
        }

        publications.withType<MavenPublication>().all {
            val publishingArtifactId = when (name) {
                "metadata" -> {
                    "$publishingArtifactIdBase-common"
                }
                "kotlinMultiplatform" -> {
                    publishingArtifactIdBase
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
