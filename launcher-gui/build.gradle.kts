plugins {
    id("gir-launcher.java-application-conventions")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

buildConfig {
    className("BuildConfig")
    packageName("eu.girc.launcher")
    useJavaOutput()

    buildConfigField<String>("VERSION", "${project.version}")
}

repositories {
    maven {
        url = uri("https://repo.u-team.info")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

javafx {
    version = "22"
    modules = listOf("javafx.controls", "javafx.web")
}

application {
    mainModule = "eu.girc.launcher"
    mainClass = "eu.girc.launcher.Launcher"
}

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("net.hycrafthd:minecraft_authenticator:3.0.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:32.1.2-jre")
}