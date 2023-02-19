plugins {
    id("com.android.library")
}

android {
    namespace = "org.lsposed.paranoid.samples.library"
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
