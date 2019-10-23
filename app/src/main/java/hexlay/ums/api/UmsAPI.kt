package hexlay.ums.api

import hexlay.ums.models.Profile
import hexlay.ums.models.Session
import hexlay.ums.models.StudentTotals
import hexlay.ums.models.subject.Subject
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UmsAPI {

    companion object {
        const val BASE_URL = "https://ums.sangu.edu.ge/"
    }

    @GET("subject/student/current")
    fun getCurrentStudentSubjects(): Observable<List<Subject>>

    @GET("subject/student/list")
    fun getTotalStudentSubjects(): Observable<List<Subject>>

    @GET("subject/student/list")
    fun getStudentTotals(): Observable<StudentTotals>

    @GET("session/student/list")
    fun getStudentSessions(): Observable<List<Session>>

    @POST("auth/login")
    @FormUrlEncoded
    fun login(
        @Field("email")
        email: String,
        @Field("password")
        password: String
    ): Observable<Profile>

    @POST("auth/logout")
    fun logout(): Observable<Response<Void>>

}