package com.troblecodings.launcher.javafx.views

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.util.Builder

class HeaderViewBuilder : Builder<Region> {
    override fun build(): Region? {
        return StackPane().apply {
            children += HBox().apply {
                spacing = 10.0
                children += Button("Test").apply {  }
                children += Button("Test2").apply {  }
                alignment = Pos.CENTER
            }
            alignment = Pos.CENTER
        }
    }
}