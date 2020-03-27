package hexlay.ums.services.events

data class SubscriptionError(var throwable: Throwable) : Event()