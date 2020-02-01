import com.google.common.base.CaseFormat
import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlinMultiPlatform
    bintray
    `maven-publish`
}

kotlin {
    jvm()

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
                implementation(TestLibs.assertkCommon)
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
                implementation(TestLibs.assertkJvm)
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
    // alias to allTests task (Kotlin MPP does not have test task)
    register("test") { dependsOn("allTests") }
}

setProperty("archivesBaseName", publishingArtifactIdBase)

bintray {
    user = bintrayUser
    key = bintrayApiKey
    publish = false
    setPublications(*Bintray.publications)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = Bintray.repo
        name = publishingArtifactIdBase
        desc = Bintray.desc
        userOrg = Bintray.userOrg
        websiteUrl = Bintray.websiteUrl
        vcsUrl = Bintray.vcsUrl
        issueTrackerUrl = Bintray.issueTrackerUrl
        githubRepo = Bintray.githubRepo
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
}
