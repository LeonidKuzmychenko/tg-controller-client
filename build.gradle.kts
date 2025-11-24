plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("edu.sc.seis.launch4j") version "3.0.6"
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-client"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

tasks.shadowJar {
    archiveBaseName.set("app")
    archiveVersion.set("")
    archiveClassifier.set("")
}

dependencies {
    implementation("io.projectreactor.netty:reactor-netty:1.2.11")
    implementation("io.projectreactor:reactor-core:3.7.12")

    implementation("com.formdev:flatlaf:3.6.2")
    implementation("com.github.oshi:oshi-core:6.9.1")
    implementation("tools.jackson.core:jackson-databind:3.0.2")

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.21")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.register<Exec>("packageExe") {
    dependsOn(tasks.shadowJar)

    val jarPath = layout.buildDirectory.file("libs/app.jar").get().asFile

    if (!jarPath.exists()) {
        throw GradleException("JAR not found at $jarPath")
    }

    commandLine(
        "jpackage",
        "--name", "MyApp3",
        "--input", jarPath.parent,     // вот здесь используется
        "--main-jar", jarPath.name,
        "--main-class", "lk.tech.tgcontrollerclient.Main",
        "--type", "exe"
    )
}