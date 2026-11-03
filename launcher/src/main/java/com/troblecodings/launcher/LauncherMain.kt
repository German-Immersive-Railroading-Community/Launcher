package com.troblecodings.launcher

import com.google.inject.Guice
import com.google.inject.Injector
import com.troblecodings.launcher.di.GirModule
import com.troblecodings.launcher.di.JavaFxModule
import com.troblecodings.launcher.di.ViewModule
import javafx.application.Application
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import java.util.concurrent.ExecutorService

class LauncherMain : Application() {
    private lateinit var injector: Injector
    private lateinit var executorService: ExecutorService

    override fun start(primaryStage: Stage) {
        val contentPane = StackPane()

        injector = Guice.createInjector(GirModule(), JavaFxModule(contentPane), ViewModule())
        executorService = injector.getInstance(ExecutorService::class.java)

        primaryStage.title = "GIRC Launcher v${BuildConfig.APP_VERSION}"
        primaryStage.show()
    }
}