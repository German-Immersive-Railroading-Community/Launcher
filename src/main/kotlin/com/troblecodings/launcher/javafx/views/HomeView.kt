package com.troblecodings.launcher.javafx.views

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.effect.ColorAdjust
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.util.Builder
import java.util.concurrent.CompletableFuture

class HomeView : Builder<BorderPane> {

    private val progressPercentLabel: Label = Label("0%")

    private val progressDescriptionLabel: Label = Label()

    private val progressBar: ProgressBar = ProgressBar(0.00).apply {
        progressProperty().addListener { _, _, new ->
            val percent = (new.toDouble() * 100.0).toInt()
            progressPercentLabel.text = "$percent%"
        }
    }

    private val launchButton: Button = Button("Launch").apply {
        onMouseClicked = EventHandler { _: MouseEvent ->
            // Disable button so that we don't accidentally start two times
            isDisable = true
            effect = ColorAdjust(1.0, -1.0, 0.0, 0.0)

            // Make sure previous progress is gone
            clearProgress()

            CompletableFuture.runAsync {
                // TODO: Replace with minecraft startup call here
                while (true) {
                    Platform.runLater { progressBar.progress += 0.01 }

                    Thread.sleep(250)
                    if (progressBar.progress >= 1.0) {
                        break
                    }
                }
            }.thenRun {
                Platform.runLater {
                    progressDescriptionLabel.text = "Done."

                    // Enable button again
                    isDisable = false
                    effect = null
                }
            }
        }
    }

    override fun build(): BorderPane {
        return BorderPane().apply {
            bottom = VBox().apply {
                padding = Insets(0.0, 8.0, 5.0, 8.0)
                spacing = 2.0
                alignment = Pos.TOP_CENTER

                children += launchButton

                children += VBox().apply {
                    children += progressBar.apply {
                        progress = 0.00

                        // HACK: this makes the progress bar take all the space available to it
                        maxWidth = Double.MAX_VALUE
                        maxHeight = Double.MAX_VALUE
                    }
                    children += HBox().apply {
                        children += progressDescriptionLabel.apply {
                            textFill = Color.LIGHTGRAY
                        }

                        children += Region().apply {
                            HBox.setHgrow(this, Priority.ALWAYS)
                        }

                        children += progressPercentLabel.apply {
                            textFill = Color.LIGHTGRAY
                        }
                    }
                }
            }
        }
    }

    private fun clearProgress() {
        progressBar.progress = 0.00
        progressDescriptionLabel.text = ""
        progressPercentLabel.text = "0%"
    }
}