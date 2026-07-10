pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases")
    }
}

plugins {
    // Definimos el plugin de toolchains para que Gradle descargue Java automáticamente si falta
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "LumineriaBase"

include("common")
include("forge")
include("neoforge")