plugins {
    java
    application
    alias(libs.plugins.javafxplugin)
    //alias(libs.plugins.shadow)
    kotlin("jvm") version "2.2.0"
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("com.github.gmazzo.buildconfig") version "5.6.7"
    id("org.beryx.jlink") version "3.1.3"
}

group = "com.troblecodings"
version = "2.0.0-dev"

buildConfig {
    className("LauncherConstants")
    packageName("com.troblecodings.launcher")

    useJavaOutput()

    buildConfigField("APP_NAME", project.name)
    buildConfigField("APP_VERSION", provider { "${project.version}" })
}

val junitVersion = "5.10.2"

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
}

//tasks.shadowJar {
//    minimize()
//    mergeServiceFiles()
//
//    from({
//        project.configurations.runtimeClasspath.get().map {
//            if (it.isDirectory) it else zipTree(it)
//        }
//    }, {
//        project.configurations.compileClasspath.get().map {
//            if (it.isDirectory) it else zipTree(it)
//        }
//    })
//}

dependencies {
    implementation(libs.commons)
    implementation(libs.dirs)
    //implementation(libs.nbt)
    implementation(libs.slf4japi)
    implementation(libs.logback)
    implementation(libs.mcauth)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome6-pack:12.4.0")
}