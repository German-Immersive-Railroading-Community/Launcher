package com.troblecodings.launcher.javafx.views

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Builder
import org.slf4j.LoggerFactory

enum class NotificationType {
    Debug, Info, Warn, Error, Fatal, Progress
}

data class Notification(val title: String, val description: String, val type: NotificationType)

class NotificationPane : Builder<StackPane> {
    val notificationList: ObservableList<Node> = FXCollections.observableArrayList()
    val logger = LoggerFactory.getLogger(NotificationPane::class.java)

    lateinit var stackPane: StackPane
    lateinit var notificationBox: VBox

    override fun build(): StackPane {
        stackPane = StackPane().apply {
            notificationBox = VBox().apply {
                isPickOnBounds = false
                padding = Insets(10.0)
                alignment = Pos.TOP_RIGHT
            }

            children += notificationBox
        }

        notificationList.addListener(this::onNotificationListChanged)

        return stackPane
    }

    private fun onNotificationListChanged(change: ListChangeListener.Change<out Node>): Unit {
        while (change.next()) {
            if (change.wasAdded()) {
                for (node in change.addedSubList) {
                    logger.debug("Adding notification")
                    addNotification(node)
                }
            } else if (change.wasRemoved()) {
                for (node in change.removed) {
                    removeNotification(node)
                }
            } else {
                logger.debug("Unhandled change listener change-type")
            }
        }
    }

    private fun addNotification(notification: Node) {

    }

    private fun removeNotification(notification: Node) {

    }
}