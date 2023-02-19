plugins {
    `java-library`
    `maven-publish`
    signing
}

val javaVersion: JavaVersion by extra

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>(rootProject.name) {
            artifactId = project.name
            group = group
            version = version
            from(components.getByName("java"))
            pom {
                name.set(project.name)
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
