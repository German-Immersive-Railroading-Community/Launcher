package com.troblecodings.launcher;

import com.troblecodings.launcher.util.FileUtil;
import com.troblecodings.launcher.util.LauncherPaths;
import javafx.application.Application;

import java.io.IOException;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws IOException {
        LauncherPaths.init();

        // This needs to happen before any loggers have the chance to be configured.
        System.setProperty("app.root", FileUtil.SETTINGS.baseDir);

        Application.launch(Launcher.class, args);
    }
}
