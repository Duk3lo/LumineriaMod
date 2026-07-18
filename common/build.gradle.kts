plugins {
    id("java-library")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

group = "org.astral.lumineriabase"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

minecraft {
    version("1.20.1")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.xerial:sqlite-jdbc:3.45.1.0")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("org.spongepowered:mixin:0.8.5")
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}