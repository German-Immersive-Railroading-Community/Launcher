package com.troblecodings.launcher.javafx

import com.troblecodings.launcher.Launcher
import com.troblecodings.launcher.util.LauncherPaths
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

val stackPane: StackPane = StackPane()
val optionalMods = ArrayList<CheckBox?>()
val modsPath: Path = LauncherPaths.getGameDataDir().resolve("mods")
val optionalModsPath: Path = LauncherPaths.getGameDataDir().resolve("optional-mods")

class OptionalModsScene : Scene {
    constructor() : super(stackPane) {
        Launcher.setupScene(this, stackPane)

        val wrapperBox = VBox().apply {
            maxWidth = 600.0
            maxHeight = 400.0
            alignment = Pos.CENTER
            style = "-fx-padding: 20px 0px;"
        }

        val optModsLabel = Label("Optional Mods")
        val scrollPane = ScrollPane().apply {
            maxWidth = 500.0
            maxHeight = 300.0
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        }

        val vbox = VBox().apply {
            prefWidth = scrollPane.maxWidth
            prefHeight = scrollPane.maxHeight
            style = "-fx-padding: 0px;"

            scrollPane.content = this
            refreshOptMods(this)
        }

        val buttonHBox = HBox(10.0)
        buttonHBox.alignment = Pos.CENTER

        val backButton = Button("Back")
        backButton.styleClass.add("optionButton")
        backButton.onAction = EventHandler { ev: ActionEvent? -> Launcher.setScene(Launcher.OPTIONSSCENE) }

        val refreshButton = Button("Refresh")
        refreshButton.styleClass.add("optionButton")
        refreshButton.onAction = EventHandler { ev: ActionEvent? -> refreshOptMods(vbox) }

        buttonHBox.children.addAll(backButton, refreshButton)

        wrapperBox.children.addAll(optModsLabel, scrollPane, buttonHBox)

        stackPane.children.add(wrapperBox)
    }

    private fun refreshOptMods(vBox: VBox) {
        try {
            if (!Files.exists(optionalModsPath)) Files.createDirectories(optionalModsPath)

            optionalMods.forEach(Consumer { mod: CheckBox? ->
                vBox.children.remove(mod)
            })

            optionalMods.clear()

            Files.list(optionalModsPath).forEach { filePath: Path? ->
                val fileName = filePath!!.toFile().getName()
                val chkBox = CheckBox()
                chkBox.isSelected = Launcher.getInstance().appSettings.optionalMods.contains(fileName)
                chkBox.text = fileName.split("\\.jar$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                chkBox.onAction = EventHandler setOnAction@{ ev: ActionEvent? ->
                    if (chkBox.isIndeterminate) return@setOnAction
                    setOptionalModState(chkBox.text, chkBox.isSelected)
                }
                optionalMods.add(chkBox)
                vBox.children.add(chkBox)
            }
        } catch (ioe: IOException) {
            Launcher.onError(ioe)
        }
    }

    // Only pass in the file name (e.g. OptiFine)!
    private fun setOptionalModState(modName: String?, state: Boolean) {
        var modName = modName
        modName += ".jar"

        val optionalModPath = optionalModsPath.resolve(modName)

        if (!Files.exists(optionalModPath)) {
            Launcher.onError(IOException("Could not find the mod $optionalModPath"))
            return
        }

        val modPath = modsPath.resolve(modName)

        try {
            if (Files.exists(modPath)) {
                Files.delete(modPath)
                Launcher.getInstance().appSettings.optionalMods.remove(modName)
            }

            if (state) {
                Files.copy(optionalModPath, modPath)
                Launcher.getInstance().appSettings.optionalMods.add(modName)
            }
        } catch (ioe: IOException) {
            Launcher.onError(ioe)
        }
    }
}