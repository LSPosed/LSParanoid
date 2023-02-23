@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin)
    `maven-publish`
    signing
}

dependencies {
    compileOnly(gradleApi())
    implementation(projects.core)
    implementation(libs.grip)
    implementation(libs.asm.common)
}

publish {
    githubRepo = "LSPosed/LSParanoid"
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
}
