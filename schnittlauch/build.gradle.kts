plugins {
    application
    id("org.openjfx.javafxplugin")
    kotlin("jvm")
}

javafx {
    version = "25"
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation("dev.dirs:directories:26")
    implementation("org.apache.commons:commons-lang3:3.20.0")
    implementation("commons-io:commons-io:2.21.0")
    implementation("org.apache.logging.log4j:log4j-api:3.0.0-beta2")
    implementation("org.apache.logging.log4j:log4j-core:3.0.0-beta3")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("org.json:json:20260814")
    implementation("net.raphimc:MinecraftAuth:5.0.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass = "com.troblecodings.launcher.Main"
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(25)
}