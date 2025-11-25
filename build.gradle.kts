plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.beryx.jlink") version "3.1.1"
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-client"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("lk.tech.tgcontrollerclient.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.tinylog:tinylog-impl:2.7.0")
    runtimeOnly("org.tinylog:slf4j-tinylog:2.7.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

jlink {
    imageName.set("tg-controller-client")
    options.set(listOf("--strip-debug", "--compress=2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "tgcontroller"
        mainClass.set(application.mainClass)
    }
    forceMerge("slf4j", "logback")
}