plugins {
    java
    application

    // ide-related plugins
    eclipse
    idea

    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.3"
    id("com.github.gmazzo.buildconfig") version "5.6.7"
}

group = "com.troblecodings"
version = "2.0.0-dev"
description = "Launcher for the GIRC modpack."

buildConfig {
    className("LauncherConstants")
    packageName("com.troblecodings.launcher")

    useJavaOutput()

    buildConfigField("APP_NAME", project.name)
    buildConfigField("APP_VERSION", provider { "${project.version}" })
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.u-team.info")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

application {
    mainModule.set("com.troblecodings.launcher")
    mainClass.set("com.troblecodings.launcher.Launcher")
}

javafx {
    version = "25"
    modules = listOf("javafx.controls")
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("dev.dirs:directories:26")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.20")
    implementation("net.raphimc:MinecraftAuth:4.1.1")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.google.guava:guava:32.1.2-jre")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "zip-6", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "GIRLauncher"
    }
}
