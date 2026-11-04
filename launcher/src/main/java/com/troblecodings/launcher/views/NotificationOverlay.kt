package com.troblecodings.launcher.views

import com.troblecodings.launcher.Notification
import com.troblecodings.launcher.NotificationManager
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class NotificationOverlay(notificationManager: NotificationManager, content: Node) : StackPane() {
    private val notificationBox = VBox(8.0)
    val notifications: ListProperty<Notification> = SimpleListProperty(this, "notifications", FXCollections.observableArrayList())

    // TODO: major jank
    init {
        notifications.bind(notificationManager.notifications)
        notifications.addListener(ListChangeListener {
            notificationBox.children.setAll(notifications)
        })

        children += content

        notificationBox.isPickOnBounds = false
        notificationBox.maxWidth = USE_PREF_SIZE
        notificationBox.maxHeight = USE_PREF_SIZE

        setAlignment(notificationBox, Pos.BOTTOM_RIGHT)
        setMargin(notificationBox, Insets(16.0))

        children += notificationBox
    }
}