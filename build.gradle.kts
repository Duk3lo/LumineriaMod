plugins {
    id("java")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8" apply false
}

group = "org.astral.lumineriabase"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}