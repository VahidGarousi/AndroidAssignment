enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        includeBuild("gradle/build-logic")
        maven("https://maven.myket.ir")
        google()
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.myket.ir")
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
rootProject.name = "AndroidCodeChallenge"
include (":app")
include(":feature:player:presentation")
include(":feature:player:domain")
include(":core:common")
include(":core:domain")
include(":core:ui")
include(":core:designsystem")
