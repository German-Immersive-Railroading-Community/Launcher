module eu.girc.launcher.core {
    requires com.google.gson;
    requires java.net.http;

    opens eu.girc.launcher.core.models to com.google.gson;
    opens eu.girc.launcher.core.models.auth to com.google.gson;
    opens eu.girc.launcher.core.models.adoptium to com.google.gson;

    exports eu.girc.launcher.core.auth;
    exports eu.girc.launcher.core.models;
    exports eu.girc.launcher.core.models.auth;
    exports eu.girc.launcher.core.models.adoptium;
}
