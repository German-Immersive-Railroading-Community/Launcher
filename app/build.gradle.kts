import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    java
    // Marks this project as a Java Application and provides tools for execution.
    application

    // The javafx configuration plugin, allows for the javafx {} code-block below.
    alias(libs.plugins.javafxplugin)
}

repositories {
    // Use maven central package repository
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    
    implementation(libs.gson)
    implementation(libs.guava)
    
    implementation(libs.atlantafx)

    implementation(libs.log4japi)
    implementation(libs.log4jcore)
    
    implementation(libs.commonslang)
}

javafx {
    version = libs.versions.javafx.get()
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainModule = "eu.girc.launcher"
    mainClass = "eu.girc.launcher.Launcher"
}


tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations.matching { it.name.contains("downloadSources") }
        .configureEach {
            attributes {
                val os = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName()
                val arch = DefaultNativePlatform.getCurrentArchitecture().name
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
                attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(OperatingSystemFamily::class, os))
                attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(MachineArchitecture::class, arch))
            }
        }