plugins {
    id("io.sentry.jvm.gradle") version "4.4.0"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
    id("org.javamodularity.moduleplugin") version "1.8.12" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = Config.Project.group
    version = properties[Config.Project.versionNameProp].toString()
    description = Config.Project.description
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.javamodularity.moduleplugin")
    apply(plugin = "io.sentry.jvm.gradle")
    apply(plugin = "com.github.gmazzo.buildconfig")
}

val sentryToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""
sentry {
    debug = true

    autoInstallation.enabled = true

    org = "girc"
    projectName = "gir-launcher-java"
    url = "https://sentry.girc.eu"

    includeDependenciesReport = true
    includeSourceContext = sentryToken.isNotBlank()
    additionalSourceDirsForSourceContext = listOf("launcher-gui/src/main/java")
    authToken = sentryToken
}