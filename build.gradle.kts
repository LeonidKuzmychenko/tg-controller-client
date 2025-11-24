plugins {
    java
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-client"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:3.6.2")
    implementation("com.github.oshi:oshi-core:6.9.1")
    implementation("io.projectreactor.netty:reactor-netty:1.2.11")
    implementation("io.projectreactor:reactor-core:3.7.12")
    implementation("tools.jackson.core:jackson-databind:3.0.2")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}