import com.adarshr.gradle.testlogger.theme.ThemeType
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    java
    // Marks this project as a Java Application and provides tools for execution.
    application

    // The javafx configuration plugin, allows for the javafx {} code-block below.
    alias(libs.plugins.javafxplugin)

    alias(libs.plugins.test.logger)

    id("org.beryx.jlink").version("3.0.1")
}

repositories {
    // Use maven central package repository
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.gson)
    implementation(libs.guava)

    implementation(libs.atlantafx)

    implementation("ch.qos.logback:logback-classic:1.5.6")

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

testlogger {
    theme = ThemeType.MOCHA
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = true
    showCauses = true
    showStandardStreams = true
    showSimpleNames = false
    showSummary = true
    showOnlySlow = false
    logLevel = LogLevel.LIFECYCLE
}

tasks.named<Test>("test") {
    useJUnitPlatform()

//    maxHeapSize = "1G"
//
//    testLogging {
//        events("passed")
//    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations.matching {
    it.name.contains("downloadSources")
}.configureEach {
    attributes {
        val os = DefaultNativePlatform.getCurrentOperatingSystem().toFamilyName()
        val arch = DefaultNativePlatform.getCurrentArchitecture().name
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class, Usage.JAVA_RUNTIME))
        attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(OperatingSystemFamily::class, os))
        attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(MachineArchitecture::class, arch))
    }
}

jlink {
    // https://github.com/beryx/badass-jlink-plugin/issues/217#issuecomment-1776917698
//    forceMerge("log4j-api")
//
//    mergedModule {
//        additive = true
//        uses("org.apache.logging.log4j.util.PropertySource")
//        uses("org.apache.logging.log4j.spi.Provider")
//        uses("org.apache.logging.log4j.message.ThreadDumpMessage.ThreadInfoFactory")
//    }

    options.addAll("--strip-debug", "--no-header-files", "--no-man-pages")
    //options.addAll("--compress", "2",)

    launcher {
        name = "girc-launcher"
        //jvmArgs = listOf("-Dlog4j.debug=true")
    }

    jpackage {
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu", "--win-shortcut"))
            imageOptions.addAll(listOf("--win-console"))
        }
    }
}