plugins {
    kotlin("jvm") version "2.0.21"
    id("com.typewritermc.module-plugin") version "1.1.3"
}

group = "de.chaos"
version = "0.1.0"


repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.typewritermc.com/releases")


}





typewriter {
    namespace = "chaos"

    extension {
        name = "Items"
        shortDescription = "Item TypeWriter Addon"
        description = """   
            |A addon for TypeWriter. This plugin provides the editing of itemstacks and adding Lore or attributes to them.
            """.trimMargin()
        engineVersion = "0.9.0-beta-162"
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
    destinationDirectory.set(file("C:/Users/julie/Desktop/server/plugins/Typewriter/extensions"))
    archiveFileName.set("ItemExtension-${version}.jar")
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

