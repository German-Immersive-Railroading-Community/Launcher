package com.troblecodings.launcher.javafx

import com.troblecodings.launcher.LauncherView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Label

class ViewManager {
    private val currentView: SimpleObjectProperty<LauncherView> = SimpleObjectProperty(LauncherView.HOME)
    private val viewCache: HashMap<LauncherView, Node> = HashMap()

    fun getCurrentView(): LauncherView {
        return currentView.get()
    }

    fun setCurrentView(view: LauncherView) {
        currentView.set(view)
    }

    fun currentViewProperty(): SimpleObjectProperty<LauncherView> {
        return currentView
    }

    fun getView(view: LauncherView): Node {
        return viewCache.getOrPut(view) { createView(LauncherView.HOME) }
    }

    fun createView(view: LauncherView): Node {
        return when (view) {
            else -> Label("Scene not found!").apply { style = "-fx-text-fill: crimson" }
        }
    }
}