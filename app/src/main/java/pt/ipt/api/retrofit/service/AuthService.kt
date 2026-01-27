package pt.ipt.api.retrofit.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Accept: application/json", "Content-Type: application/json")
    @POST("api/Authentication/login")
    fun login(@Body body: LoginRequest):Call<LoginResponse>

    @POST("api/Authentication/register")
    fun register(@Body body: RegisterRequest):Call<RegisterResponse>
}

data class LoginRequest(
    val username: String,
    val password: String
)
data class LoginResponse(
    val token: Token,
    val role: String
)

data class RegisterResponse(
    val message: String,
    val token: Token
)


data class RegisterRequest(
    val username: String,
    val password: String,
    val isArtista: Boolean,
    val email: String
)

data class ApiError(
    val errors: List<String>,

    val message: String
)

data class Token(
    val result: String,
    val id: Int,
    val exception: String?,
    val status: Int,
    val isCanceled: Boolean,
    val isCompleted: Boolean,
    val isCompletedSuccessfully: Boolean,
    val creationOptions: Int,
    val asyncState: String?,
    val isFaulted: Boolean
)