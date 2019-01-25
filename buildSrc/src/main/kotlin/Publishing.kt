import org.gradle.api.Project

private const val majorVersion: Int = 0
private const val minorVersion: Int = 1
private val patchVersion = 1
private const val coreModuleName = "core"

val Project.publishingGroupId: String by lazy { "com.github.uzzu.kortex" }

val Project.publishingArtifactVersion: String
    get() = "$majorVersion.$minorVersion.$patchVersion"

val Project.publishingArtifactId: String
    get() = project.rootProject.name.let {
        // ArtifactId becomes like below:
        // :core -> ${rootProject.name}
        // :core-foo -> ${rootProject.name}-foo
        project.name.replace(coreModuleName, it)
    }

object MavenPublications {
    const val publicationName = "maven"
    const val description = "Coroutines techniques"
    const val url = "https://github.com/uzzu/kortex"
    const val license = "The Apache Software License, Version 2.0"
    const val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    const val licenseDistribution = "repo"
    const val developersId = "uzzu"
    const val developersName = "Hirokazu Uzu"
    const val organization = developersId
    const val organizationUrl = "https://uzzu.github.io"
    const val scmUrl = "https://github.com/uzzu/kortex"
}

val Project.bintrayUser: String?
    get() = findProperty("bintrayUser") as String?
val Project.bintrayApiKey: String?
    get() = findProperty("bintrayApiKey") as String?

object Bintray {
    const val mavenUrl = "https://dl.bintray.com/uzzu/maven"
    val publications = arrayOf(MavenPublications.publicationName)
    const val repo = "maven"
    const val desc = MavenPublications.description
    const val userOrg = MavenPublications.organization
    const val websiteUrl = MavenPublications.url
    const val issueTrackerUrl = "https://github.com/uzzu/kortex/issues"
    const val vcsUrl = "https://github.com/uzzu/kortex.git"
    const val githubRepo = "uzzu/kortex"
    val licenses = arrayOf("Apache-2.0")
    val labels = arrayOf("kotlin", "android")
    val publicDownloadNumbers = true
}

