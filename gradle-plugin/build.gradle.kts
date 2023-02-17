import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val androidToolsVersion: String by extra
val javaVersion: JavaVersion by extra
val kotlinVersion: String by extra

plugins {
    idea
    kotlin("jvm") version ("1.8.0")
    `maven-publish`
    signing
}

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

dependencies {
    implementation(project(":core"))
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:$androidToolsVersion")
    compileOnly("com.android.tools.build:gradle-api:$androidToolsVersion")
    implementation(project(":processor"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    annotationProcessor("com.google.auto.service:auto-service:1.0.1")
    implementation("com.google.auto.service:auto-service-annotations:1.0.1")
}

val generatedDir = File(projectDir, "generated")
val generatedJavaSourcesDir = File(generatedDir, "main/java")

val genTask = tasks.register("generateBuildClass") {
    inputs.property("version", version)
    outputs.dir(generatedDir)
    doLast {
        val buildClassFile =
            File(generatedJavaSourcesDir, "org/lsposed/lsparanoid/plugin/Build.java")
        buildClassFile.parentFile.mkdirs()
        buildClassFile.writeText(
            """package org.lsposed.lsparanoid.plugin;
               |public class Build {
               |   public static final String VERSION = "$version";
               |}""".trimMargin()
        )
    }
}

sourceSets {
    main {
        java {
            srcDir(generatedJavaSourcesDir)
        }
    }
}

tasks.withType(KotlinCompile::class.java) {
    dependsOn(genTask)
}

idea {
    module {
        generatedSourceDirs.add(generatedJavaSourcesDir)
    }
}

publishing {
    publications {
        register<MavenPublication>("lsparanoid") {
            artifactId = "gradle-plugin"
            group = group
            version = version
            pom {
                name.set("gradle-plugin")
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
