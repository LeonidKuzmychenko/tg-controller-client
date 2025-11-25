plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    implementation("io.projectreactor.netty:reactor-netty:1.2.11")
    implementation("io.projectreactor:reactor-core:3.7.12")
    implementation("com.github.oshi:oshi-core:6.9.1")
    implementation("tools.jackson.core:jackson-databind:3.0.2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.21")
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