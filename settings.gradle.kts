rootProject.name = "lsparanoid"

include(":core", ":processor", ":gradle-plugin")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}
