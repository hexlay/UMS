package hexlay.ums.api

class AccessDeniedException : Exception() {
    override val message: String?
        get() = "აუტორიზაციისას მოხდა შეცდომა"
}
class UnauthorizedException : Exception() {
    override val message: String?
        get() = "გასაგრძელებლად საჭიროა აუტორიზაცია"
}
class NotFoundException : Exception() {
    override val message: String?
        get() = "სერვერთან დაკავშირებისას მოხდა შეცდომა"
}