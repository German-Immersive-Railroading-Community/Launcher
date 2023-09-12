module eu.girc.launcher {
    requires com.google.gson;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires org.apache.logging.log4j;
    requires java.base;
    requires java.desktop;
    requires net.hycrafthd.minecraft_authenticator;
    requires org.json;
    requires NBT;

    opens eu.girc.launcher.util to com.google.gson;
    opens eu.girc.launcher to javafx.graphics;
}