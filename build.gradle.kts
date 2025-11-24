plugins {
    java
    id("org.graalvm.buildtools.native") version "0.10.2"
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-client"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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

graalvmNative {
    metadataRepository {
        enabled.set(true)
    }

    binaries {
        named("main") {
            imageName.set("tg-controller-client")

            mainClass.set("lk.tech.tgcontrollerclient.Main")

            buildArgs.addAll(
                "--no-fallback",
                "--enable-url-protocols=http,https",
                "--enable-native-access=ALL-UNNAMED",

                // Netty MUST be runtime initialized
                "--initialize-at-run-time=reactor.netty",
                "--initialize-at-run-time=io.netty",

                // Jackson (динамическое отражение)
                "--initialize-at-run-time=com.fasterxml.jackson",

                // FlatLaf GUI
                "--initialize-at-run-time=com.formdev.flatlaf",

                // Твой код
                "--initialize-at-run-time=lk.tech",

                "-H:+ReportExceptionStackTraces",

//                "--initialize-at-build-time=io.netty.util.AsciiString",
//                $$"--initialize-at-build-time=io.netty.util.AsciiString$2",
//                $$"--initialize-at-build-time=io.netty.util.AsciiString$1",
//                "--initialize-at-build-time=io.netty.util.internal.logging.InternalLogLevel",
//                "--initialize-at-build-time=java.net.Inet4Address",
            )

            verbose.set(true)
        }
    }

    agent {
        enabled.set(true)
    }
}