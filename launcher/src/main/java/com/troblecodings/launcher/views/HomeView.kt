package com.troblecodings.launcher.views

import com.google.inject.Inject
import com.troblecodings.launcher.ViewManager
import com.troblecodings.launcher.utils.StylesheetUtils
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.apache.logging.log4j.LogManager
import kotlin.jvm.java

class HomeView @Inject constructor(
    private val viewManager: ViewManager
) : VBox(), IView {
    override fun initialize() {
        LogManager.getLogger(HomeView::class.java).debug("Initializing HomeView")
        StylesheetUtils.addRequired(this, HomeView::class.java)
        styleClass.add("home-view")

        this.apply {
            children += Label("Welcome to the launcher!")
        }
    }

    override fun getView(): Parent = this
}