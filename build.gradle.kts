plugins {
    java
    application
    alias(libs.plugins.javafxplugin)

    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("com.github.gmazzo.buildconfig") version "5.6.7"
    id("org.beryx.jlink") version "3.1.3"

    kotlin("jvm") version "2.2.0"
}

group = "com.troblecodings"
description = "Launcher for the GIRC modpack."

version = "2.0.0-dev"

buildConfig {
    className("LauncherConstants")
    packageName("com.troblecodings.launcher")

    useJavaOutput()

    buildConfigField("APP_NAME", project.name)
    buildConfigField("APP_VERSION", provider { "${project.version}" })
}

val junitVersion = "5.10.2"
val os = org.gradle.internal.os.OperatingSystem.current()

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

application {
    mainModule.set("com.troblecodings.launcher")
    mainClass.set("com.troblecodings.launcher.Launcher")
}

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls")
}

jlink {
    addExtraDependencies("javafx")

    options.addAll(listOf("--compress", "zip-6", "--strip-debug", "--no-header-files", "--no-man-pages"))

    launcher {
        name = "GIRC-Launcher"
    }

    jpackage {
        skipInstaller = false

        imageName = "GIRC-Launcher"
        installerName = "GIRC-Launcher"
        appVersion = project.version.toString().replace("-dev", "")

        installerOptions.add("--verbose")

        if (os.isWindows) {
            icon = "icon.ico"
            installerType = "msi"
            installerOptions.addAll(listOf("--win-dir-chooser", "--win-menu", "--win-shortcut-prompt", "--win-per-user-install"))
        } else if (os.isLinux) {
            icon = "icon.png"
            installerType = "deb"
            installerOptions.addAll(listOf("--linux-shortcut", "--linux-menu-group", "Games", "--linux-deb-maintainer", "shiro@shirosaka.dev"))
        } else {
            throw GradleException("Unsupported OS")
        }
    }
}

dependencies {
    implementation(libs.commons)
    implementation(libs.dirs)
    implementation(libs.slf4japi)
    implementation(libs.logback)
    implementation(libs.mcauth)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")
}