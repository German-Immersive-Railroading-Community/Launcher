package com.troblecodings.launcher.javafx

import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.javafx.views.HomeView
import com.troblecodings.launcher.javafx.views.LoginView
import com.troblecodings.launcher.javafx.views.SettingsView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Label
import org.slf4j.LoggerFactory

class ViewManager {
    private val currentView: ObjectProperty<LauncherView> = SimpleObjectProperty(LauncherView.NONE)
    private val viewCache: HashMap<LauncherView, Node> = HashMap()
    private val logger = LoggerFactory.getLogger(ViewManager::class.java)

    fun getCurrentView(): LauncherView {
        return currentView.get()
    }

    fun setCurrentView(view: LauncherView) {
        if (view == currentView.get()) {
            logger.debug("Already on view {}", view)
            return;
        }

        logger.debug("Switching views: {} -> {}", currentView.get(), view)
        currentView.set(view)
    }

    fun currentViewProperty(): ObjectProperty<LauncherView> {
        return currentView
    }

    fun getViewNode(view: LauncherView): Node {
        return viewCache.getOrPut(view) { createView(view) }
    }

    private fun createView(view: LauncherView): Node {
        return when (view) {
            LauncherView.HOME -> HomeView().build()
            LauncherView.SETTINGS -> SettingsView().build()
            else -> Label("Scene not found!").apply { style = "-fx-text-fill: crimson" }
        }
    }
}