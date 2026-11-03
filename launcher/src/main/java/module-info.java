module com.troblecodings.launcher {
    requires java.net.http;
    requires java.desktop;
    requires jakarta.inject;

    requires javafx.controls;
    requires javafx.graphics;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires com.google.gson;
    requires com.google.guice;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    requires com.fasterxml.jackson.databind;

    requires dev.dirs;

    requires org.apache.commons.io;
    requires org.apache.commons.lang3;

    requires httpclient;
    requires MinecraftAuth;
    requires org.json;

    requires kotlin.stdlib;

    opens com.troblecodings.launcher.views to com.google.guice, com.fasterxml.jackson.databind;
    exports com.troblecodings.launcher.views;

    opens com.troblecodings.launcher.models to com.google.gson;
    opens com.troblecodings.launcher to com.google.guice, com.fasterxml.jackson.databind, javafx.graphics;
    opens com.troblecodings.launcher.di to com.fasterxml.jackson.databind, com.google.guice, javafx.graphics;
    opens com.troblecodings.launcher.services to com.google.guice, com.fasterxml.jackson.databind;
    exports com.troblecodings.launcher;

}