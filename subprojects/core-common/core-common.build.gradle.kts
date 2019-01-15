import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    id(PluginsId.kotlinMultiPlatformCommon)
    id(PluginsId.bintray)
    `maven-publish`
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

// region publish

base {
    archivesBaseName = publishingArtifactId
}

afterEvaluate {
    var sourceJar: Task? = null
    tasks {
        sourceJar = create("sourceJar", type = Jar::class) {
            dependsOn("classes")
            classifier = "sources"
            setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE)
            from(sourceSets.getByName("main").withGroovyBuilder {
                getProperty("kotlin")
            })
        }
        withType<Jar> {
            archiveName = publishingArtifactId
        }
    }

    bintray {
        user = bintrayUser
        key = bintrayApiKey
        publish = true
        setPublications(*Bintray.publications)
        pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
            repo = Bintray.repo
            name = publishingArtifactId
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

    publishing {
        repositories {
            maven(url = Bintray.mavenUrl)
        }

        publications {
            register(MavenPublications.publicationName, MavenPublication::class) {
                from(components.getByName("java"))
                groupId = publishingGroupId
                artifactId = publishingArtifactId
                version = publishingArtifactVersion
                artifact(sourceJar)
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

// endregion
