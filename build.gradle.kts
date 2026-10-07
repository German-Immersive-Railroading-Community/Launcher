plugins {
    id("org.openjfx.javafxplugin") version "0.1.0" apply false
}

group = "eu.girc.launcher"
version = "0.1.0"

allprojects {
    repositories { mavenCentral() }
}

subprojects {
    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
        modularity.inferModulePath = true
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    dependencies {
        add("testImplementation", platform("org.junit:junit-bom:6.0.0"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }
}
