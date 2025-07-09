package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.LauncherView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.util.Builder

class Sidebar : Builder<Parent> {
    var currentView: SimpleObjectProperty<LauncherView> = SimpleObjectProperty()

    override fun build(): Parent? = StackPane().apply {
        background = Background(BackgroundFill(Color.DARKSLATEGRAY, null, null))
    }
}