plugins {
    id("com.android.library")
    id("org.lsposed.lsparanoid")
}

lsparanoid {
    global = true
}

android {
    namespace = "org.lsposed.paranoid.samples.library_obfuscate"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
