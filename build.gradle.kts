plugins {
    id("java")
    id("application")
    id("io.sentry.jvm.gradle") version "4.6.0"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
//    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("eclipse")
    id("idea")
}

group = "eu.girc"

val versionName: String by properties
version = versionName

val sentryToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""
sentry {
    debug = true

    autoInstallation.enabled = true

    org = "girc"
    projectName = "gir-launcher-java"
    url = "https://sentry.girc.eu"

    includeDependenciesReport = true
    includeSourceContext = sentryToken.isNotBlank()
    authToken = sentryToken
}

buildConfig {
    className("BuildConfig")
    packageName("eu.girc.launcher")
    useJavaOutput()

    buildConfigField<String>("VERSION", "$version")
    buildConfigField<String>("GROUP", "$group")
}

javafx {
    version = "22"
    modules = listOf("javafx.controls")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
   // mainModule = "eu.girc.launcher"
    mainClass = "eu.girc.launcher.Launcher"
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

val junitVersion = "5.10.2"
dependencies {
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.google.guava:guava:33.2.0-jre")

    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-Xlint:deprecation")
    }

    test {
        useJUnitPlatform()
    }
}

