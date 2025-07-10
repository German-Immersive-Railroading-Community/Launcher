package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.LauncherView
import com.troblecodings.launcher.services.UserService
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Builder

class Sidebar(val userService: UserService) : Builder<StackPane> {
    val currentView: SimpleObjectProperty<LauncherView> = SimpleObjectProperty()

    override fun build(): StackPane? = StackPane().apply {
        stylesheets += Sidebar::class.java.getResource("sidebar.css")?.toExternalForm()

        prefWidth = 1280 * 0.20

        background = Background(
            BackgroundFill(
                Color.hsb(12.0, 10.0 / 100.0, 19.0 / 100.0), null, null
            )
        )

        children += VBox().apply {
            alignment = Pos.CENTER

            children += Region().apply { VBox.setVgrow(this, Priority.ALWAYS) }

            children += VBox().apply {
                alignment = Pos.CENTER
                spacing = 0.0
                padding = Insets(0.0, 0.0, 5.0, 0.0)

//                setStyle("-fx-border-color: gray; -fx-border-style: none none solid none; -fx-border-width: 2px; ")

                children += Separator().apply {
                    translateY = 20.0
                    padding = Insets(0.0, 7.0, 0.0, 7.0)
                }

                children += ImageView(Image(Sidebar::class.java.getResourceAsStream("/com/troblecodings/launcher/girc_logo_w_1.png"))).apply {
                    translateY = 10.0
                }

                children += Label("GIRC Launcher v1.1.0").apply { style = "-fx-text-fill: gray;" }
            }
        }
    }
}