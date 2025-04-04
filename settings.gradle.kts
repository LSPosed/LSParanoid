enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "LSParanoid"

include(
    ":core",
    ":processor",
    ":gradle-plugin",
    ":samples:application",
    ":samples:application-global-obfuscate",
    ":samples:library",
    ":samples:library-may-obfuscate",
    ":samples:library-obfuscate"
)
