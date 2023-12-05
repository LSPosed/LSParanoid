import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.tasks.Jar

plugins {
    idea
    alias(libs.plugins.kotlin)
    `java-gradle-plugin`
    `maven-publish`
    signing
}


dependencies {
    implementation(projects.core)
    implementation(projects.processor)
    implementation(libs.kotlin.api)
    implementation(libs.agp.api)
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
            """
            package org.lsposed.lsparanoid.plugin;
            /**
             * The type Build.
             */
            public class Build {
               /**
                * The constant VERSION.
                */
               public static final String VERSION = "$version";
            }""".trimIndent()
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

publish {
    githubRepo = "LSPosed/LSParanoid"
    publishPlugin("$group", rootProject.name, "org.lsposed.lsparanoid.plugin.LSParanoidPlugin") {
        name = rootProject.name
        description = "String obfuscator for Android applications"
        url = "https://github.com/LSPosed/LSParanoid"
        licenses {
            license {
                name = "Apache License 2.0"
                url = "https://github.com/LSPosed/LSParanoid/blob/master/LICENSE.txt"
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
