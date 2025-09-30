plugins {
    kotlin("jvm") version "2.0.21"
    id("com.typewritermc.module-plugin") version "2.0.0"
}

group = "de.chaos"
version = "0.2.0"


repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.typewritermc.com/releases")


}





typewriter {
    namespace = "chaos"

    extension {
        name = "CustomItems"
        shortDescription = "Item TypeWriter Addon"
        description =
            "A addon for TypeWriter. This plugin provides custom items such as swords, bows armor and other things"
        engineVersion = "0.9.0-beta-165"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA

        dependencies {
        }

        paper()
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    destinationDirectory.set(file("C:/Users/julie/Desktop/dev/plugins/Typewriter/extensions"))
    archiveFileName.set("CustomItemExtension-${version}.jar")
}
tasks.register<Copy>("copyJarToServer") {
    dependsOn("jar")
    val jar = tasks.named<Jar>("jar").get()
    from(jar.destinationDirectory)
    include(jar.archiveFileName.get())
    into("C:/Users/julie/Desktop/dev/plugins/Typewriter/extensions")

    doFirst {
        println("→ Kopiere ${jar.archiveFileName.get()} von ${jar.destinationDirectory.get().asFile}")
    }

}

