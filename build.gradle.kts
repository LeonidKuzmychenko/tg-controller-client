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
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:2.2.1")
    implementation("tools.jackson.core:jackson-databind:3.0.2")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}