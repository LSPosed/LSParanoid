import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension


@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.lsplugin.publish)
    alias(libs.plugins.kotlin) apply false
}


allprojects {
    group = "org.lsposed.lsparanoid"
    version = "0.5.1"

    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    plugins.withType(KotlinPluginWrapper::class.java) {
        extensions.configure(KotlinJvmProjectExtension::class.java) {
            jvmToolchain(17)
        }
    }
}
