plugins {
    application
    id("org.openjfx.javafxplugin")
    id("org.javamodularity.moduleplugin")
    id("org.beryx.jlink") version "4.1.1"
    kotlin("jvm")
}

javafx {
    version = "25"
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.1.0")

    implementation(kotlin("stdlib"))

    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome5-pack:12.4.0")

    implementation("dev.dirs:directories:26")

    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("commons-io:commons-io:2.21.0")

    implementation("org.apache.logging.log4j:log4j-api:2.25.5")
    implementation("org.apache.logging.log4j:log4j-core:2.25.5")

    implementation("com.google.code.gson:gson:2.14.0")
    implementation("com.google.inject:guice:7.0.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.4")

    implementation("org.json:json:20260814")

    implementation("net.raphimc:MinecraftAuth:5.0.2")

    testImplementation(kotlin("test"))
}

application {
    mainModule = "com.troblecodings.launcher"
    mainClass = "com.troblecodings.launcher.GirMain"
}

kotlin {
    jvmToolchain(25)
}

    jlink {
        // zip-6 is basically --compress=2, but integers have been deprecated here
        options.addAll("--compress", "zip-6", "--no-header-files", "--no-man-pages", "--generate-cds-archive", "--strip-debug")
        addExtraDependencies("javafx")

        launcher {
            name = "GIRC-Launcher"
        }

        jpackage {
            installerName = "GIRC-Launcher"
            vendor = "German Immersive Railroading Community"
            appVersion = project.version.toString()

            // This is because windows the big stoopid doesnt support any semver fancies
            if (appVersion.contains('+')) {
                appVersion = appVersion.split('+').first()
            }

            if (appVersion.contains('-')) {
                appVersion = appVersion.split('-').first()
            }

            installerOptions.addAll("--license-file", project.rootDir.resolve("LICENSE").toString())

            val os = org.gradle.internal.os.OperatingSystem.current()
            if (os.isWindows) {
                installerOptions.addAll("--icon", project.projectDir.resolve("src/main/resources/images/icon.ico").toString())
                installerOptions.addAll("--win-shortcut", "--win-dir-chooser", "--win-menu", "--win-per-user-install")
            }
        }
    }