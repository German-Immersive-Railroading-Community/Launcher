package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import javafx.util.Builder

// TODO: Hook sidebar buttons here and change views based on the button clicked (change the center pane)
class MainView(val viewManager: ViewManager, val userService: UserService) : Builder<Parent> {
    private val sidebar: Sidebar = Sidebar(viewManager, userService)
    private val loginView: LoginView = LoginView(userService, viewManager) // TODO: switch parameters

    private val mainPane: BorderPane = BorderPane().apply {
        style = "-fx-background-color: hsb(30.0, 8.7%, 9.02%)"
    }

    override fun build(): Parent {
        viewManager.currentViewProperty().addListener { _, _, newValue ->
            mainPane.center = viewManager.getViewNode(newValue)
        }

        return mainPane.apply {
            left = sidebar.build()
            center = loginView.build()
        }
    }
}