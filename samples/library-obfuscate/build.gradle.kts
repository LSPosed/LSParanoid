plugins {
    id("org.lsposed.lsparanoid")
    id("com.android.library")
}

lsparanoid {
    classFilter = { true }
}

android {
    namespace = "org.lsposed.paranoid.samples.library_obfuscate"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
