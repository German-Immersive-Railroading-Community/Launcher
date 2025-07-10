module com.troblecodings.launcher {
    requires com.google.common;
    requires com.google.gson;
    requires dev.dirs;
    requires java.desktop;
    requires java.net.http;
    requires httpclient;
    requires MinecraftAuth;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;

    requires kotlin.stdlib;
//    requires kotlin.reflect;
//    requires kotlinx.coroutines.core;
//    requires kotlin.native_utils;

    opens com.troblecodings.launcher.models to com.google.gson;
//    opens com.troblecodings.launcher.models.girjson to com.google.gson;
//    opens com.troblecodings.launcher.models.minecraft to com.google.gson;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    opens com.troblecodings.launcher to javafx.graphics;
}