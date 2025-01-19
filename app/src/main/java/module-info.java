module eu.girc.launcher {
    requires java.desktop;
    requires java.prefs;
    requires java.base;
    requires jdk.zipfs;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.lang3;

    requires com.google.gson;
    opens eu.girc.launcher.models to com.google.gson;

    requires atlantafx.base;

    // https://github.com/beryx/badass-jlink-plugin/issues/217#issuecomment-1776917698
    requires java.compiler;
    requires java.naming;

    requires org.slf4j;

    opens eu.girc.launcher;
    opens eu.girc.launcher.layout;
    opens eu.girc.launcher.ui;
    //opens eu.girc.launcher.page;
}