package hexlay.ums.api

import hexlay.ums.models.Exam
import hexlay.ums.models.Profile
import hexlay.ums.models.StudentTotals
import hexlay.ums.models.notifications.NotificationBase
import hexlay.ums.models.session.Session
import hexlay.ums.models.subject.Subject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface UmsAPI {

    companion object {
        const val BASE_URL = "https://ums.sangu.edu.ge/"
    }

    @GET("subject/student/current")
    fun getCurrentStudentSubjects(): Observable<List<Subject>>

    @GET("subject/student/list")
    fun getTotalStudentSubjects(): Observable<List<Subject>>

    @GET("subject/student/list")
    fun getTotalStudentSubjectsPrevijous(
        @Query("student")
        student: String
    ): Observable<List<Subject>>

    @GET("subject/totals")
    fun getStudentTotals(): Observable<StudentTotals>

    @GET("session/student/list")
    fun getStudentSessions(): Observable<List<Session>>

    @GET("exams/student/list")
    fun getStudentExams(): Observable<List<Exam>>

    @GET("notification/received")
    fun getNotifications(
        @Query("state")
        state: String? = "unread",
        @Query("limit")
        limit: String = "20",
        @Query("page")
        page: String = "1"
    ): Observable<NotificationBase>

    @POST("auth/login")
    @FormUrlEncoded
    fun login(
        @Field("email")
        email: String,
        @Field("password")
        password: String
    ): Observable<Profile>

    @POST("auth/password/change")
    @FormUrlEncoded
    fun passwordChange(
        @Field("password")
        password: String
    ): Observable<Response<Void>>

    @POST("notification/mark/{id}")
    @FormUrlEncoded
    fun markNotification(
        @Path("id")
        id: String,
        @Field("state")
        state: String? = "read"
    ): Observable<Response<Void>>

    @POST("auth/logout")
    fun logout(): Observable<Response<Void>>

}