package com.troblecodings.launcher.javafx.views

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.util.Builder

class SettingsView : Builder<VBox> {
    private val javaMemoryEnabled: BooleanProperty = SimpleBooleanProperty(false)

    fun javaMemoryEnabledProperty(): BooleanProperty = javaMemoryEnabled;

    fun isJavaMemoryEnabled(): Boolean = javaMemoryEnabled.get()

    fun setJavaMemoryEnabled(value: Boolean) = javaMemoryEnabled.set(value)

    override fun build(): VBox = VBox().apply {
        children += TabPane().apply {
            tabDragPolicy = TabPane.TabDragPolicy.FIXED
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tabs += Tab("Java").apply {
                tooltip = Tooltip("Java path and arguments for Minecraft")
                content = VBox().apply {
                    maxWidth = Double.MAX_VALUE
                    maxHeight = Double.MAX_VALUE
                    children += HBox().apply {
                        children += Label("Hi")
                    }
                }
            }
        }
    }
}