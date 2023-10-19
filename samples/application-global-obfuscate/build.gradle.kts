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
    compileSdk = 33
    buildToolsVersion = "33.0.2"
    defaultConfig {
        applicationId = "org.lsposed.paranoid.samples.application_global_obfuscate"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    compileOnly("androidx.annotation:annotation:1.5.0")
    implementation(project(":library"))
    implementation(project(":library-obfuscate"))
    implementation(project(":library-may-obfuscate"))
}
