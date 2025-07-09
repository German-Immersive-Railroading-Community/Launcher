package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.javafx.ViewManager
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.util.Builder

class MainView(viewManager: ViewManager) : Builder<Parent> {
    override fun build(): Parent? = BorderPane().apply {
        center = Label("Test")
    }
}