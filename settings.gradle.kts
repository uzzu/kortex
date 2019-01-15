applyPluginManagement()
enableFeaturePreview("STABLE_PUBLISHING")
enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "kortex"

// region subprojects

fun Settings.includeSubProject(name: String) {
    include(":$name")
    project(":$name").projectDir = File("$rootDir/subprojects/$name")
    project(":$name").buildFileName = "$name.build.gradle.kts"
}

includeSubProject("core-common")
includeSubProject("core-jvm")
includeSubProject("core-test")

// endregion

// region examples

fun Settings.includeExampleProject(name: String, dir: String) {
    include(":examples:$name")
    project(":examples:$name").projectDir = File("$rootDir/examples/$dir")
    project(":examples:$name").buildFileName = "$name.build.gradle.kts"
}

// endregion
