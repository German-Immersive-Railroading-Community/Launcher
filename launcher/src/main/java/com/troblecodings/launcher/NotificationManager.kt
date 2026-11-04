package com.troblecodings.launcher

import com.google.inject.Inject
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper
import javafx.collections.FXCollections
import javafx.scene.layout.Region
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.util.*


enum class NotificationType {
    NONE, INFO, WARNING, ERROR, PROGRESS
}

abstract class Notification(
    val type: NotificationType = NotificationType.NONE,
    val duration: Duration = Duration.ofSeconds(5),
    val notificationId: UUID = UUID.randomUUID()
) : Region() {
    protected fun notificationCard(): Region {
        return Region().apply {
            styleClass += "notification-card"

        }
    }
}

class ProgressNotification(
    private val progress: ReadOnlyDoubleProperty,
    duration: Duration = Duration.ofSeconds(5),
    notificationId: UUID = UUID.randomUUID()
) : Notification(NotificationType.PROGRESS, duration, notificationId) {
    companion object {
        private val logger = LogManager.getLogger(ProgressNotification::class.java)
    }

    init {
        logger.info("ProgressNotification created with id: $notificationId")
    }
}

class NotificationManager @Inject constructor() {
    companion object {
        private val logger = LogManager.getLogger(NotificationManager::class.java)
    }

    private val notificationItems = FXCollections.observableArrayList<Notification>()
    private val notificationItemsProperty = ReadOnlyListWrapper(this, "notifications", notificationItems)

    val notifications: ReadOnlyListProperty<Notification>
        get() = notificationItemsProperty.readOnlyProperty

    fun notify(notification: Notification): Notification {
        logger.info("Notifying: ${notification.id}")
        notificationItems += notification
        return notification
    }

    fun dismiss(notification: Notification) {
        notificationItems.removeIf { it.id == notification.id }
    }

    fun dismiss(notificationId: UUID) {
        notificationItems.removeIf { it.notificationId == notificationId }
    }

    fun clear() {
        notificationItems.clear()
    }
}