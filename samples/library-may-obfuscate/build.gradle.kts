plugins {
    id("com.android.library")
}

android {
    namespace = "org.lsposed.paranoid.samples.library_may_obfuscate"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.lsparanoid.core)
}
