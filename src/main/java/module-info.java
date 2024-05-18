module eu.girc.launcher {
    requires sentry;

    requires com.google.gson;
    requires com.google.common;

    requires org.apache.commons.compress;
    requires org.apache.commons.lang3;
    requires org.apache.logging.log4j;

    requires java.base;
    requires java.desktop;
    requires java.net.http;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    opens eu.girc.launcher.models to com.google.gson;
    opens eu.girc.launcher.models.auth to com.google.gson;
    opens eu.girc.launcher.models.adoptium to com.google.gson;
}
