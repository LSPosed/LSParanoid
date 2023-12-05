plugins {
    `java-library`
    `maven-publish`
    signing
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
                name = project.name
                description = "String obfuscator for Android applications"
                url = "https://github.com/LSPosed/LSParanoid"
                licenses {
                    license {
                        name = "Apache License 2.0"
                        url ="https://github.com/LSPosed/LSParanoid/blob/master/LICENSE.txt"
                    }
                }
                developers {
                    developer {
                        name = "LSPosed"
                        url = "https://lsposed.org"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/LSPosed/LSParanoid.git"
                    url = "https://github.com/LSPosed/LSParanoid"
                }
            }
        }
    }
}
