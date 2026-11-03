package com.troblecodings.launcher.views

import com.google.inject.Inject
import com.troblecodings.launcher.ViewManager
import com.troblecodings.launcher.services.IUserServiceKt
import com.troblecodings.launcher.utils.StylesheetUtils
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.*
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon

class MainWindow @Inject constructor(
    private val viewManager: ViewManager,
    private val userService: IUserServiceKt
) : BorderPane() {
    init {
        StylesheetUtils.addRequired(this, javaClass, "MainWindow.css")

        left = sidebar().apply {
            prefWidthProperty().bind(this@MainWindow.widthProperty().multiply(0.2))
        }
    }

    private fun sidebar(): Pane = VBox().apply {
        children += VBox(12.0).apply {
            alignment = Pos.CENTER
            children += HBox(8.0).apply {
                alignment = Pos.CENTER
                styleClass += "main-window-vbox-hover"

                setOnMouseClicked { _ -> handleNavigation(ViewId.HOME) }

                children += FontIcon(FontAwesomeSolid.HOME)
                children += Label("Home")
            }

            children += HBox(8.0).apply {
                alignment = Pos.CENTER
                styleClass += "main-window-vbox-hover"

                setOnMouseClicked { _ -> handleNavigation(ViewId.SETTINGS) }

                children += FontIcon(FontAwesomeSolid.COGS)
                children += Label("Settings")
            }
        }

        children += Pane().apply { VBox.setVgrow(this, Priority.ALWAYS) }

        children += HBox().apply {
            alignment = Pos.BASELINE_CENTER
            children += Label(userService.profile?.name ?: "Guest")
        }
    }

    private fun handleNavigation(view: ViewId) = viewManager.show(view)
}

