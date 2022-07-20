import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.7.10"
    signing
}

group = "net.jfsanchez.netty"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-codec-http:4.1.79.Final")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.create("sourcesJar", Jar::class.java) {
    dependsOn(tasks.dokkaJavadoc)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.create("javadocJar", Jar::class.java) {
    dependsOn(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc.get().outputDirectory)
}

tasks.build {
    dependsOn("javadocJar", "sourcesJar")
}

tasks.publish {
    dependsOn(tasks.build)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            artifact(tasks.get("javadocJar"))
            artifact(tasks.get("sourcesJar"))

            from(components["kotlin"])

            pom {
                name.set(project.name)
                description.set("Netty plain text logging handler")
                url.set("https://github.com/jfsanchez91/plaintext-logging-handler")
                version = project.version.toString()
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/jfsanchez91/plaintext-logging-handler/master/LICENSE")
                    }
                }
                organization {
                    name.set("net.jfsanchez")
                    url.set("https://jfsanchez.net")
                }
                developers {
                    developer {
                        id.set("jfsanchez")
                        name.set("Jorge F. Sánchez")
                        email.set("mail@jfsanchez.net")
                    }
                }
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/jfsanchez91/plaintext-logging-handler/issues")
                }
                scm {
                    connection.set("scm:git:https://github.com/jfsanchez91/plaintext-logging-handler.git")
                    developerConnection.set("sscm:git:https://github.com/jfsanchez91/plaintext-logging-handler.git")
                    url.set("https://github.com/jfsanchez91/plaintext-logging-handler.git")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}
