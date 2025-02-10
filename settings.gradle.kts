pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "SoulSearching"
include(":domain")
include(":local-android")
include(":shared-ui")
include(":desktopApp")
include(":local-desktop")
include("core-ui")
include("repository")
include("shared-di")
include("features")
include("features:playback")
findProject(":features:playback")?.name = "playback"
include("features:filemanager")
findProject(":features:filemanager")?.name = "filemanager"
include("features:musicmanager")
findProject(":features:musicmanager")?.name = "musicmanager"
include("remote")
