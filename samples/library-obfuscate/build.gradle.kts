plugins {
    id("org.lsposed.lsparanoid")
    id("com.android.library")
}

lsparanoid {
    global = true
}

android {
    namespace = "org.lsposed.paranoid.samples.library_obfuscate"
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
