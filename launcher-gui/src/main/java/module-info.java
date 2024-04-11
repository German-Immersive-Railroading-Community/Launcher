module eu.girc.launcher {
    requires eu.girc.launcher.core;

    requires com.google.gson;
    requires com.google.common;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires org.apache.commons.lang3;
    requires org.apache.commons.compress;
    requires java.base;
    requires java.desktop;
    requires java.net.http;
    requires net.hycrafthd.minecraft_authenticator;
    requires sentry;

    requires java.compiler;
    requires java.naming;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens eu.girc.launcher to javafx.graphics;
}