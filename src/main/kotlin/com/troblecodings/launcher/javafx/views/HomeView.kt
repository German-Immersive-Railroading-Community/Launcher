package com.troblecodings.launcher.javafx.views

import com.troblecodings.launcher.Launcher
import com.troblecodings.launcher.javafx.tasks.McStartInfo
import com.troblecodings.launcher.javafx.tasks.McStartTask
import javafx.concurrent.Task
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

class HomeView : Builder<BorderPane> {
    private var startTask: Task<McStartInfo>? = null
    private var startThread: Thread? = null

    // Progress fields
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

            startTask = McStartTask()

            startTask?.progressProperty()?.addListener { _, _, new ->
                progressBar.progress = new as Double
            }

            startTask?.messageProperty()?.addListener { _, _, new ->
                progressDescriptionLabel.text = new
            }

            startTask?.onFailed = EventHandler {
                // TODO: Log this
                progressDescriptionLabel.text = startTask?.exception?.message

                // Enable button again
                isDisable = false
                effect = null
            }
            startTask?.onCancelled = EventHandler {
                progressDescriptionLabel.text = "Cancelled."

                // Enable button again
                isDisable = false
                effect = null
            }

            startTask?.onSucceeded = EventHandler {
                it.source.value
                progressDescriptionLabel.text = "Done."

                // Enable button again
                isDisable = false
                effect = null
            }

            Launcher.getAppExecutor().execute(startTask!!)
        }
    }

    private fun clearProgress() {
        progressBar.progress = 0.00
        progressDescriptionLabel.text = ""
        progressPercentLabel.text = "0%"
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
}