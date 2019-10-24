package hexlay.ums.models.session

data class Session(
    var startTime: String,
    var dayOfWeek: Int,
    var room: Room,
    var sessionGroup: SessionGroup
)