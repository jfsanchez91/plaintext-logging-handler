import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.7.10"
}

group = "net.jfsanchez.netty"
version = "1.0-SNAPSHOT"

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            artifact(tasks.get("javadocJar"))
            artifact(tasks.get("sourcesJar"))

            from(components["kotlin"])
        }
    }
}
