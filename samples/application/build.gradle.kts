plugins {
    id("com.android.application")
    id("org.lsposed.lsparanoid")
}

lsparanoid {
    includeDependencies = true
    variantFilter = { variant -> variant.name == "release" }
}

android {
    namespace = "org.lsposed.paranoid.samples.application"
    compileSdk = 33
    defaultConfig {
        applicationId = "org.lsposed.paranoid.samples.application"
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
    compileOnly("androidx.annotation:annotation:1.9.1")
    implementation(project(":samples:library"))
    implementation(project(":samples:library-obfuscate"))
    implementation(project(":samples:library-may-obfuscate"))
}
