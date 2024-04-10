plugins {
    id("com.github.gmazzo.buildconfig") version "3.0.3"
    id("io.sentry.jvm.gradle") version "4.4.0"
}

allprojects {
    repositories {
        mavenCentral()
    }

    group = Config.Project.group
    version = properties[Config.Project.versionNameProp].toString()
    description = Config.Project.description
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