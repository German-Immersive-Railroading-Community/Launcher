module com.troblecodings.launcher {
    requires com.google.common;
    requires com.google.gson;

    // Package to retrieve uniform, per platform file paths, e.g. $XDG_CONFIG_DIR, etc.
    requires dev.dirs;

    requires jdk.crypto.ec;
    requires java.desktop;
    requires java.net.http;
    requires httpclient;
    requires MinecraftAuth;
    requires org.apache.commons.lang3;

    // Logging
    // This is required by SLF4J. Without this, a jpackage image fails to start
    requires java.naming;
    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;

    requires kotlin.stdlib;

    opens com.troblecodings.launcher.models to com.google.gson;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    // le icons
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome6;

    requires jsr305;

    opens com.troblecodings.launcher to javafx.graphics;
}