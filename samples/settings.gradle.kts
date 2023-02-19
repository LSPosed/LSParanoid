pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
rootProject.name = "samples"
include(":application", ":application-global-obfuscate", ":library-obfuscate", ":library", ":library-may-obfuscate")
