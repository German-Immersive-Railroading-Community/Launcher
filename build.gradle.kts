import com.github.gmazzo.buildconfig.BuildConfigExtension

plugins {
    id("org.openjfx.javafxplugin") version "0.1.0" apply false
    id("org.javamodularity.moduleplugin") version "2.1.0" apply false
    id("com.github.gmazzo.buildconfig") version "6.1.2" apply false
}

allprojects {
    repositories { mavenCentral() }
}

subprojects {
    apply{
        plugin("java")
        plugin("com.github.gmazzo.buildconfig")
    }

    group = "com.troblecodings"
    version = "2.0.0-dev"

    extensions.configure<BuildConfigExtension> {
        buildConfigField("APP_NAME", project.name)
        buildConfigField("APP_VERSION", provider { "${project.version}" })

    }

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
