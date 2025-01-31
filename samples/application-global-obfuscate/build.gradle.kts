plugins {
    id("com.android.application")
    id("org.lsposed.lsparanoid")
}

lsparanoid {
    classFilter = { true }
    includeDependencies = true
    variantFilter = { variant -> variant.name == "release" }
}

android {
    namespace = "org.lsposed.paranoid.samples.application_global_obfuscate"
    compileSdk = 35
    defaultConfig {
        applicationId = "org.lsposed.paranoid.samples.application_global_obfuscate"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    compileOnly(libs.annotation)
    implementation(project(":samples:library"))
    implementation(project(":samples:library-obfuscate"))
    implementation(project(":samples:library-may-obfuscate"))
}
