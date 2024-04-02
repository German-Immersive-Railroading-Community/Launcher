import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("io.sentry.jvm.gradle") version "4.4.0"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("com.adarshr.test-logger") version "3.2.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.0.1"
}

group = "eu.girc.launcher"
version = "1.1.0-dev"

sentry {
    debug = true
    telemetry = false
    includeSourceContext = true
    org = "girc"
    projectName = "gir-launcher-java"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
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

buildConfig {
    useJavaOutput()
    packageName("eu.girc.launcher")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
}

javafx {
    version = "22"
    modules = listOf("javafx.controls", "javafx.web")
}

application {
    mainModule = "eu.girc.launcher"
    mainClass = "eu.girc.launcher.Launcher"
}

tasks {
    // Make Sentry compatible with Build Configs
    generateSentryBundleIdJava {
        dependsOn(generateBuildConfig)
    }

    test {
        useJUnitPlatform()

        testlogger {
            theme = ThemeType.MOCHA_PARALLEL
        }
    }
}

val junitVersion = "5.9.2"

dependencies {
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("net.hycrafthd:minecraft_authenticator:3.0.6")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:32.1.2-jre")

    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val os = org.gradle.internal.os.OperatingSystem.current()
jlink {
    imageZip = file("${layout.buildDirectory}/image-zip/gir-launcher.zip")

    launcher {
        name = "GIR Launcher"
    }

    options = listOf("--bind-services", "--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages", "--ignore-signing-information")
    forceMerge("log4j-api")
    jpackage {
        vendor = "German-Immersive-Railroading-Community and Contributors"

        if (os.isWindows) {
            installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut"))
            imageOptions.add("--win-console")
        }
    }
}