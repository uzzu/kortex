import org.gradle.api.Project

private const val majorVersion: Int = 0
private const val minorVersion: Int = 1
private val patchVersion = 0
private const val coreModuleName = "core"

val Project.publishingGroup: String by lazy { "com.github.uzzu.cortex" }

val Project.publishingArtifactVersion: String
    get() = "$majorVersion.$minorVersion.$patchVersion"

val Project.publishingArtifactId: String
    get() = project.rootProject.name.let {
        // ArtifactId becomes like below:
        // :core -> ${rootProject.name}
        // :core-foo -> ${rootProject.name}-foo
        project.name.replace(coreModuleName, it)
    }
