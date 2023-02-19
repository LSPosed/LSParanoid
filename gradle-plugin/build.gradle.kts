import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val androidToolsVersion: String by extra
val javaVersion: JavaVersion by extra

plugins {
    idea
    kotlin("jvm")
    `java-gradle-plugin`
    `maven-publish`
    signing
}

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":processor"))
    compileOnly("com.android.tools.build:gradle-api:$androidToolsVersion")
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

tasks.withType(Jar::class.java) {
    dependsOn(genTask)
}

idea {
    module {
        generatedSourceDirs.add(generatedJavaSourcesDir)
    }
}

gradlePlugin {
    plugins {
        create(rootProject.name) {
            id = project.group as String
            implementationClass = "org.lsposed.lsparanoid.plugin.ParanoidPlugin"
        }
    }
}

publishing {
    publications {
        val gradlePluginName = "pluginMaven" // https://github.com/gradle/gradle/issues/10384
        create<MavenPublication>(gradlePluginName) {
            artifactId = project.name
            group = group
            version = version
            pom {
                description.set("String obfuscator for Android applications")
                url.set("https://github.com/LSPosed/LSParanoid")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/LSPosed/LSParanoid/blob/master/LICENSE.txt")
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
