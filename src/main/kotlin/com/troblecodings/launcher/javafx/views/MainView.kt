package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.util.Builder

// TODO: Hook sidebar buttons here and change views based on the button clicked (change the center pane)
class MainView(val viewManager: ViewManager, val userService: UserService) : Builder<Parent> {
    private val currentView: SimpleObjectProperty<LauncherView> = SimpleObjectProperty(if (userService.isValidSession) LauncherView.HOME else LauncherView.LOGIN)

    private val sidebar: Sidebar = Sidebar(userService)

    private val mainPane: BorderPane = BorderPane().apply {
        background = Background(BackgroundFill(Color.hsb(30.0, 8.7 / 100, 9.02 / 100), CornerRadii(0.0), Insets(0.0)))
    }

    override fun build(): Parent {
        return mainPane.apply {
            left = sidebar.build()
            center = SettingsView().build()
        }
    }
}