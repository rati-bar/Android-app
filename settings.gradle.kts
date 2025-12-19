pluginManagement {
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

rootProject.name = "ScreenTimeCalculator"

// App modules
include(":app-parent")
include(":app-child")

// Core modules
include(":core:model")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:designsystem")
