package hexlay.ums.services.events

import hexlay.ums.models.notifications.Notification

class NotificationRemoveEvent(val notification: Notification) : Event()