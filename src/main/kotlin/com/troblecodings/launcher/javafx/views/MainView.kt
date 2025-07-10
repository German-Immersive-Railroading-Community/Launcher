package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.util.Builder

class MainView(val viewManager: ViewManager, val userService: UserService) : Builder<Parent> {
    override fun build(): Parent? = BorderPane().apply {
        left = Sidebar(userService).build()
        center = StackPane().apply {
//            background = Background(
//                BackgroundFill(
//                    Color.hsb(12.0, 10.0 / 100.0, 19.0 / 100.0), null, null
//                )
//            )
            style = "-fx-border-radius: 15 0 0 15; -fx-background-color: white;"
        }
    }
}