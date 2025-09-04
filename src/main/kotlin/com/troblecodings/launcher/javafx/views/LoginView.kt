package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Builder
import java.util.concurrent.CompletableFuture

class LoginView(val userService: UserService, val viewManager: ViewManager) : Builder<BorderPane> {
    private val deviceCode: StringProperty = SimpleStringProperty("")

    override fun build(): BorderPane = BorderPane().apply {

        center = VBox().apply {
            alignment = Pos.CENTER
            children += VBox().apply {
                maxWidth = VBox.USE_PREF_SIZE
                padding = Insets(25.0)
                alignment = Pos.CENTER
                style = "-fx-background-color: hsb(12.0, 10%, 19%); -fx-background-radius: 12px;"

                children += Label("Login with Microsoft").apply {
                    style = "-fx-font-size: 32px; -fx-text-fill: white;"
                }

                children += Region().apply {
                    minHeight = 50.0
                }

                children += Button("Login").apply {
                }
            }
        }
//        if(userService.isValidSession) {
//            Platform.runLater { viewManager.setCurrentView(LauncherView.HOME) }
//            return@apply
//        }
//
//        val future = CompletableFuture.runAsync {
//            userService.login { code ->
//                Platform.runLater { deviceCode.set(code.deviceCode) }
//            }
//        }.thenRun {
//            Platform.runLater { viewManager.setCurrentView(LauncherView.HOME) }
//        }
//
//        future.cancel(true)


    }
}