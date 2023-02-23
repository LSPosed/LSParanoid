enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("agp", "com.android.tools.build:gradle-api:7.4.1")
            library("grip", "com.joom.grip:grip:0.9.1")
            library("asm-common", "org.ow2.asm:asm-commons:9.4")
            plugin("lsplugin-publish", "org.lsposed.lsplugin.publish").version("1.0")
            plugin("kotlin", "org.jetbrains.kotlin.jvm").version("1.8.10")
        }
    }
}
rootProject.name = "lsparanoid"

include(":core", ":processor", ":gradle-plugin")
