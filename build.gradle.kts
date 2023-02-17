allprojects {
    group = "org.lsposed.lsparanoid"
    version = "0.4.0"

    val javaVersion by extra(JavaVersion.VERSION_11)
    val kotlinVersion by extra("1.8.0")
    val pabloVersion by extra("1.3.1")

    val asmVersion by extra("9.2")
    val gripVersion by extra("0.8.1")
    val logbackVersion by extra("1.4.5")

    val junitVersion by extra("4.13.2")

    val androidToolsVersion by extra("7.4.1")
    val androidxAppcompatVersion by extra("1.4.0")
    val androidxAnnotationVersion by extra("1.3.0")

    val androidxRulesVersion by extra("1.4.0")
    val androidxRunnerVersion by extra("1.4.0")
    val androidxTestExtJunitVersion by extra("1.1.3")
    val androidxEspressoVersion by extra("3.4.0")

    val androidBuildToolsVersion by extra("33.0.1")
    val androidCompileSdkVersion by extra(33)
    val androidMinSdkVersion by extra(16)
    val androidTargetSdkVersion by extra(androidCompileSdkVersion)

    buildscript {
        repositories {
            google()
            mavenLocal()
            mavenCentral()
        }

        dependencies {
            classpath("com.android.tools.build:gradle:$androidToolsVersion")
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        }
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}
