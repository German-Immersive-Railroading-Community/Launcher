package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.Launcher
import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.javafx.ViewManager
import com.troblecodings.launcher.services.UserService
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.Builder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class LoginView(val userService: UserService, val viewManager: ViewManager) : Builder<BorderPane> {
    private val logger: Logger = LoggerFactory.getLogger(LoginView::class.java)

    private var loginFuture: CompletableFuture<Void>? = null

    private val deviceCodeField: TextField = TextField().apply {
        isEditable = false
    }

    private val deviceCodeLink: Hyperlink = Hyperlink().apply {
        onMouseClicked = EventHandler { _ ->
            Launcher.getInstance().hostServices.showDocument(text)
        }
    }

    private val directDeviceCodeLink: Hyperlink = Hyperlink().apply {
        onMouseClicked = EventHandler { _ ->
            Launcher.getInstance().hostServices.showDocument(text)
        }
    }

    private val loginButton: Button = Button("Login").apply {
        onMouseClicked = EventHandler { _ ->
            loginDetailsBox.children.setAll(deviceCodeBox)

            loginFuture = CompletableFuture.runAsync {
                userService.login {
                    Platform.runLater {
                        deviceCodeField.text = it.userCode
                        deviceCodeLink.text = it.verificationUri
                        directDeviceCodeLink.text = it.directVerificationUri
                    }
                }
            }.thenRun {
                Platform.runLater { viewManager.setCurrentView(LauncherView.HOME) }
            }
        }
    }

    private val cancelButton: Button = Button("Cancel").apply {
        onMouseClicked = EventHandler { _ ->
            // Try to cancel future if present
            loginFuture?.cancel(true)
            loginDetailsBox.children.setAll(loginButton)
            logger.debug("Login cancelled")
        }
    }

    private val deviceCodeBox: VBox = VBox().apply {
        alignment = Pos.CENTER
        spacing = 3.0

        children += HBox().apply {
            children += Label("Device code: ")
            children += deviceCodeField
        }

        children += HBox().apply {
            children += Label("Enter here: ")
            children += deviceCodeLink
        }

        children += HBox().apply {
            children += Label("or click ")
            children += directDeviceCodeLink
        }

        children += Hyperlink()

        children += cancelButton.apply {
            alignment = Pos.CENTER
        }
    }

    private val loginDetailsBox: HBox = HBox().apply {
        children += loginButton
    }

    override fun build(): BorderPane = BorderPane().apply {
        if(userService.isLoggedIn) {
            viewManager.setCurrentView(LauncherView.HOME)
            return@apply
        }

        val css = LoginView::class.java.getResource("loginview.css")

        if (css != null) stylesheets += css.toExternalForm()
        else logger.error("Could not load LoginView css!")

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

                children += loginDetailsBox.apply {
                    alignment = Pos.CENTER
                }
            }
        }
    }
}