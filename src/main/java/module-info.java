module eu.girc.launcher {
    requires com.google.gson;
    requires com.google.common;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires org.apache.logging.log4j;
    requires org.apache.commons.lang3;
    requires java.base;
    requires java.desktop;
    requires java.net.http;
    requires net.hycrafthd.minecraft_authenticator;

    exports eu.girc.launcher.models to com.google.gson;

    opens eu.girc.launcher to javafx.graphics;
}