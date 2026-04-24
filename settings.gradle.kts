pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ForPet"
include(":app")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":core:data")
include(":core:database")
include(":core:background")
include(":core:ui")
include(":feature:home")
include(":feature:calendar")
include(":feature:mypet")
include(":feature:schedule")
include(":feature:walk")
