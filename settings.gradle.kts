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
            val kotlinVersion = version("kotlin", "1.8.10")
            library("agp-api", "com.android.tools.build:gradle-api:7.4.2")
            library("grip", "com.joom.grip:grip:0.9.1")
            library("asm-common", "org.ow2.asm:asm-commons:9.4")
            library("kotlin-api", "org.jetbrains.kotlin", "kotlin-gradle-plugin-api").versionRef(kotlinVersion)
            plugin("lsplugin-publish", "org.lsposed.lsplugin.publish").version("1.1")
            plugin("kotlin", "org.jetbrains.kotlin.jvm").versionRef(kotlinVersion)
        }
    }
}
rootProject.name = "lsparanoid"

include(":core", ":processor", ":gradle-plugin")
