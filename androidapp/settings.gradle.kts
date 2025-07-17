pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
            credentials {
                username = "truongdaivy57@gmail.com"
                password = "DLDrfR9QB5nhvBS"
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven {
            url = uri("https://jitpack.io")
            credentials {
                username = "truongdaivy57@gmail.com"
                password = "DLDrfR9QB5nhvBS"
            }
        }
    }
}

rootProject.name = "PRM392MNLV"
include(":app")
