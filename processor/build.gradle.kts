plugins {
    kotlin("jvm") version "1.8.0"
    `maven-publish`
    signing
}

val javaVersion: JavaVersion by extra
val asmVersion: String by extra
val kotlinVersion: String by extra
val gripVersion: String by extra
val logbackVersion: String by extra
val junitVersion: String by extra
val androidToolsVersion: String by extra

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

dependencies {
    implementation(project(":core"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.joom.grip:grip:$gripVersion")
    implementation("org.ow2.asm:asm:$asmVersion")
    implementation("org.ow2.asm:asm-commons:$asmVersion")
    testImplementation("junit:junit:$junitVersion")
}

publishing {
    publications {
        register<MavenPublication>("lsparanoid") {
            artifactId = "processor"
            group = group
            version = version
            pom {
                name.set("processor")
                description.set("String obfuscator for Android applications")
                url.set("https://github.com/LSPosed/LSParanoid")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/libxposed/service/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("LSPosed")
                        url.set("https://lsposed.org")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/LSPosed/LSParanoid.git")
                    url.set("https://github.com/LSPosed/LSParanoid")
                }
            }
            afterEvaluate {
                from(components.getByName("java"))
            }
        }
    }
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials(PasswordCredentials::class)
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LSPosed/LSParanoid")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}
