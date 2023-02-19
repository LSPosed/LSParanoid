plugins {
    id("com.android.library")
}

android {
    namespace = "org.lsposed.paranoid.samples.library_may_obfuscate"
    compileSdk = 33
    buildToolsVersion = "33.0.2"
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("org.lsposed.lsparanoid:core:0.5.0")
}
