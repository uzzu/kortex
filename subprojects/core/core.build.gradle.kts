import com.google.common.base.CaseFormat
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(pluginDeps.plugins.android.library.get().pluginId)
    id(pluginDeps.plugins.kotlin.multiplatform.get().pluginId)
    id(pluginDeps.plugins.dokka.get().pluginId)
    `maven-publish`
    signing
}

android {
    compileSdk = AndroidSdk.compileSdkVersion

    defaultConfig {
        minSdk = AndroidSdk.minSdkVersion
        targetSdk = AndroidSdk.targetSdkVersion
        consumerProguardFiles("proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
        }
        getByName("test") {
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
                implementation(deps.kotlinx.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(testDeps.kotlin.test.common)
                implementation(testDeps.kotlin.test.annotation.common)
                implementation(testDeps.assertk)
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }

        val jvmTest by getting {
            dependencies {
                runtimeOnly(testDeps.junit5.engine)
                implementation(testDeps.kotlin.test.junit5)
                implementation(testDeps.kotlin.reflect.jvm)
                implementation(testDeps.junit5.api)
                implementation(testDeps.junit5.params)
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }

        val androidTest by getting {
            dependencies {
                runtimeOnly(testDeps.junit5.engine)
                implementation(testDeps.kotlin.test.junit5)
                implementation(testDeps.kotlin.reflect.jvm)
                implementation(testDeps.junit5.api)
                implementation(testDeps.junit5.params)
            }
        }
        val androidAndroidTestRelease by getting

        androidTest.dependsOn(androidAndroidTestRelease)

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

lateinit var dokkaJar: TaskProvider<Jar>
tasks {
    named<Test>("jvmTest") {
        useJUnitPlatform()
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
            )
        }
    }
    val dokkaJavadoc = getByName("dokkaJavadoc", DokkaTask::class)
    dokkaJar = register("dokkaJar", Jar::class) {
        archiveClassifier.set("javadoc")
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc.outputDirectory)
    }
}

setProperty("archivesBaseName", publishingArtifactIdBase)

afterEvaluate { // workaround for AGP to resolve android library artifactId correctly.
    publishing {
        repositories {
            maven {
                url = env.PUBLISH_PRODUCTION.orNull()
                    ?.run { uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/") }
                    ?: uri("https://oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = env.OSSRH_USERNAME.orElse("")
                    password = env.OSSRH_PASSWORD.orElse("")
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
            version = publishingArtifactVersion(env.PUBLISH_PRODUCTION.isPresent)

            artifact(dokkaJar.get())

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

signing {
    if (env.PUBLISH_PRODUCTION.isPresent) {
        setRequired { gradle.taskGraph.hasTask("publish") }
        sign(publishing.publications)

        @Suppress("UnstableApiUsage")
        useInMemoryPgpKeys(
            env.SIGNING_KEYID.orElse(""),
            env.SIGNING_KEY.orElse(""),
            env.SIGNING_PASSWORD.orElse("")
        )
    }
}
