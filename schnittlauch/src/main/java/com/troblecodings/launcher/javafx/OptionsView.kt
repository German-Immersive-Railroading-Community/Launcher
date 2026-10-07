package com.troblecodings.launcher.javafx

import com.troblecodings.launcher.Launcher
import com.troblecodings.launcher.assets.Assets
import com.troblecodings.launcher.util.FileUtil
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.util.Duration
import javafx.util.StringConverter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Toolkit

class OptionsView : Scene(stackPane) {
    companion object {
        val stackPane: StackPane = StackPane()
        val log: Logger = LogManager.getLogger()
    }

    private val javaLocationField = TextField(FileUtil.SETTINGS.javaSettings.jreLocation)

    init {
        Launcher.setupScene(this, stackPane)
        stackPane.apply {
            children += ScrollPane().apply sp@{
                maxWidth = 650.0
                maxHeight = 450.0
                hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED

                content = VBox().apply {
                    spacing = 10.0
                    prefWidth = this@sp.maxWidth
                    StackPane.setAlignment(this, Pos.CENTER_LEFT)

                    // Java stuff

                    children += Label("Java").apply {
                        style = "-fx-padding: 0px;"
                    }

                    children += Label("Pfad").apply {
                        style = "-fx-padding: 0px; -fx-font-weight: normal; -fx-font-size: 14pt;"
                    }

                    children += HBox().apply {
                        spacing = 5.0

                        children += javaLocationField.apply {
                            font = Font.font(16.0)
                            HBox.setHgrow(this, Priority.ALWAYS)
                        }

                        children += Button("Durchsuchen").apply {
                            styleClass += "optionButton"
                            onAction = evHandler@{ _ ->
                                val chooser = FileChooser().apply {
                                    title = "Durchsuchen nach Java-Installation"
                                    extensionFilters.add(
                                        FileChooser.ExtensionFilter(
                                            "Java executable (java, javaw)",
                                            "java.exe",
                                            "java",
                                            "javaw.exe",
                                            "javaw"
                                        )
                                    )
                                }

                                val file = chooser.showOpenDialog(Launcher.getStage()) ?: return@evHandler;
                                FileUtil.SETTINGS.javaSettings.jreLocation = file.absolutePath
                                javaLocationField.text = FileUtil.SETTINGS.javaSettings.jreLocation
                                log.debug(
                                    "Setting custom launcher path to {}",
                                    FileUtil.SETTINGS.javaSettings.jreLocation
                                )
                            }
                        }
                    }

                    children += Label("RAM").apply {
                        padding = Insets(0.0)
                        style = "-fx-padding: 0px; -fx-font-weight: normal; -fx-font-size: 14pt;"
                    }

                    val infoLabel = Label("Max. RAM: ${FileUtil.SETTINGS.ram} MB").apply {
                        style = "-fx-padding: 0px; -fx-font-size: 12pt; -fx-font-weight: normal;"
                    }

                    children += infoLabel

                    children += Slider(4 * 1024.0, 24 * 1024.0, FileUtil.SETTINGS.ram.toDouble()).apply ramSlider@{
                        isShowTickLabels = true
                        isShowTickMarks = true
                        majorTickUnit = 4.0 * 1024
                        minorTickCount = 3
                        blockIncrement = 1024.0
                        isSnapToTicks = true

                        labelFormatter = object : StringConverter<Double>() {
                            override fun toString(n: Double?): String = String.format("%.0f MB", n)
                            override fun fromString(string: String?): Double = 0.0
                        }

                        valueProperty().addListener { _, _, new ->
                            FileUtil.SETTINGS.ram = new.toInt()
                            infoLabel.text = "Max. RAM: ${new.toInt()} MB"
                            log.debug("Max. memory changed to ${new.toInt()} MB")
                        }
                    }

                    children += Label("Java-Argumente").apply {
                        padding = Insets(0.0)
                        style = "-fx-font-weight: normal; -fx-font-size: 18px;"
                    }

                    children += TextArea(FileUtil.SETTINGS.javaSettings.jreArgs).apply ta@{
                        promptText = "Extra Argumente hier hinzufügen."
                        isWrapText = true
                        prefHeight = 120.0
                        val pauseTransition = PauseTransition(Duration.millis(300.0)).apply {
                            onFinished = { _ ->
                                log.debug("Changing custom jvm arguments: {}", this@ta.text)
                                FileUtil.SETTINGS.javaSettings.jreArgs = this@ta.text
                            }
                        }

                        textProperty().addListener { _ -> pauseTransition.playFromStart() }
                    }

                    // Resolution

                    children += Label("Auflösung").apply {
                        style = "-fx-padding: 20px 0px 10px 0px;"
                    }


                    children += ComboBox<String>().apply {
                        isEditable = true
                        editor.text = "${FileUtil.SETTINGS.width}x${FileUtil.SETTINGS.height}"

                        onAction = EventHandler { _ ->
                            val text = editor.text
                            if (text.contains("x")) {
                                val arr: Array<String> = text.split("x").dropLastWhile { it.isEmpty() }.toTypedArray()
                                FileUtil.SETTINGS.width = arr[0].toInt()
                                FileUtil.SETTINGS.height = arr[1].toInt()
                            }

                            log.debug("Changed resolution to ${editor.text}")
                        }

                        val dimension = Toolkit.getDefaultToolkit().screenSize
                        for (i in 2..6) {
                            items += "${dimension.width * 2 / i}x${dimension.height * 2 / i}"
                        }
                    }

                    children += Label("Misc. Einstellungen").apply {
                        style = "-fx-padding: 20px 0px 10px 0px;"
                    }

                    children += HBox().apply {
                        spacing = 10.0

                        children += Button("Logout").apply {
                            styleClass += "optionButton"
                            onAction = { _ ->
                                log.debug("Logout requested")

                                try {
                                    Launcher.getInstance().userService.logout()
                                    Platform.runLater { Launcher.setScene(Launcher.LOGINSCENE) }
                                } catch (e: Exception) {
                                    Launcher.onError(e)
                                }

                                Header.setVisibility(false)
                            }
                        }

                        children += Button("Reset").apply {
                            styleClass += "optionButton"
                            onAction = EventHandler { _ ->
                                log.debug("File reset requested")
                                FileUtil.resetFiles()
                            }
                        }

                        children += Button("Optionale Mods").apply {
                            styleClass += "optionButton"
                            onAction = { _ ->
                                log.debug("OptionalMods scene requested")
                                Launcher.setScene(Launcher.OPTIONALMODSSCENE)
                            }
                        }
                    }
                }
            }

            children += ImageView(Assets.getImage("images/train3.png")).apply {
                scaleX = -1.0
                translateX = (-1280 / 1.75) + image.width
                translateY = 360 - image.height
            }
        }
    }
}