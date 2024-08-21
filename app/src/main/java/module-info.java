module eu.girc.launcher {
    requires java.desktop;
    requires java.prefs;
    requires java.base;
    requires java.compiler;
    requires jdk.zipfs;
    
    requires javafx.controls;
    requires javafx.fxml;
    
    requires org.apache.commons.lang3;
    
    requires com.google.gson;
    opens eu.girc.launcher.models to com.google.gson;
    
    requires atlantafx.base;
    
    opens eu.girc.launcher;
    opens eu.girc.launcher.layout;
    //opens eu.girc.launcher.page;
}