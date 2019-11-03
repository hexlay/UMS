package hexlay.ums.api

class AccessDeniedException : Exception() {
    override val message: String?
        get() = "User is not allowed to request"
}
class UnauthorizedException : Exception() {
    override val message: String?
        get() = "User is unathorized"
}
class NotFoundException : Exception() {
    override val message: String?
        get() = "Unable to connect to server"
}