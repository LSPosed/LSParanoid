import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin


plugins {
    alias(libs.plugins.lsplugin.publish)
    alias(libs.plugins.kotlin) apply false
}

allprojects {
    group = "org.lsposed.lsparanoid"
    version = "0.6.0"

    
  // Configure Java to use our chosen language level. Kotlin will automatically pick this up.
  // See https://kotlinlang.org/docs/gradle-configure-project.html#gradle-java-toolchains-support
  plugins.withType<JavaBasePlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
      toolchain.languageVersion = JavaLanguageVersion.of(21)
    }
  }
}
