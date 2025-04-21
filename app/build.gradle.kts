plugins {
    application

    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
}

javafx {
    version = "23"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass = "eu.girc.launcher.Launcher"
    mainModule = "eu.girc.launcher"
}

dependencies {
    // JSON handling
    implementation("com.google.code.gson:gson:2.13.0")

    // Utilities
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("org.apache.commons:commons-lang3:3.17.0")

    implementation("com.google.inject:guice:7.0.0")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}