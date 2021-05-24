import org.gradle.api.Project

private const val majorVersion: Int = 0
private const val minorVersion: Int = 5
private val patchVersion: Int = 0
private const val coreModuleName = "core"

val Project.publishingGroupId: String
    get() = "co.uzzu.kortex"

val Project.publishingArtifactVersion: String
    get() = "$majorVersion.$minorVersion.$patchVersion"

val Project.publishingArtifactIdBase: String
    get() = project.rootProject.name.let {
        // ArtifactId becomes like below:
        // :core -> ${rootProject.name}
        // :core-foo -> ${rootProject.name}-foo
        project.name.replace(coreModuleName, it)
    }

fun Project.publishingArtifactVersion(isPublishProduction: Boolean): String =
    if (isPublishProduction) {
        publishingArtifactVersion
    } else {
        "$publishingArtifactVersion-SNAPSHOT"
    }

object MavenPublications {
    const val description = "Coroutines techniques"
    const val url = "https://github.com/uzzu/kortex"
    const val license = "The Apache Software License, Version 2.0"
    const val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    const val licenseDistribution = "repo"
    const val developersId = "uzzu"
    const val developersName = "Hirokazu Uzu"
    const val organization = developersId
    const val organizationUrl = "https://uzzu.co"
    const val scmUrl = "https://github.com/uzzu/kortex"
}
