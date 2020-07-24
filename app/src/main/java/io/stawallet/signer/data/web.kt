package io.stawallet.signer.data

import android.os.Build
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import io.stawallet.signer.Application
import io.stawallet.signer.R
import io.stawallet.signer.STAWALLET_API_URL
import io.stawallet.signer.application
import kotlinx.coroutines.Deferred
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HTTP
import retrofit2.http.Header
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DeferredIsResult")
interface StawalletV1ApiClient {
    /**
     * Membership
     */
    @HTTP(method = "GET", path = "clients/me", hasBody = false)
    fun me(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<User>

    @FormUrlEncoded
    @HTTP(method = "POST", path = "sessions", hasBody = true)
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("otp") otp: String? = null
    ): Deferred<TokenResponse>

    @FormUrlEncoded
    @HTTP(method = "REGISTER", path = "clients", hasBody = true)
    fun registerNewClient(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("referralCode") referralCode: String? = null,
        @Field("captchaResponse") captchaResponse: String? = null
    ): Deferred<User>


}

val cookieJar by lazy {
    PersistentCookieJar(
        SetCookieCache(),
        SharedPrefsCookiePersistor(application)
    )
}

val userAgent by lazy {
    String.format(
        Locale.US,
        "%s/%s (Android %s; %s; %s %s; %s)",
        Application.APPLICATION_ID,
        Application.VERSION_NAME,
        Build.VERSION.RELEASE,
        Build.MODEL,
        Build.BRAND,
        Build.DEVICE,
        //FIXME: Locale might be changed throw the settings. Then we should refresh the following value:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            application.resources.configuration.locales[0].toString()
        else
            application.resources.configuration.locale.toString()
    )
}

val okHttpClient by lazy {
    OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor { chain ->

            val request = chain.request()
            val newRequest = request
                .newBuilder()
                .addHeader("X-Firebase-Token", sessionManager.fbToken ?: "")
                .url(request.url.toString())
                .build()

            val response = chain.proceed(newRequest)

            val newToken = response.header("X-New-JWT-Token")
            if (newToken != null) {
                sessionManager.login(newToken)
            }

            // FIXME: More strict check (sometimes 401 is about s.th else, e.g. forgetting passing the authorization header)
            // FIXME: More strict check (sometimes 400 means unauthorized)
            if (response.code == 401 || (response.code == 400 && arrayOf(
                    "bad-authorization-token",
                    "bad-refresh-token"
                ).contains(response.headers["X-Reason"]))
            ) {
                // TODO: Force logout
                try {
                    sessionManager.whipSession()
                    stawalletDatabase.wipeDb()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return@addInterceptor response
        }
        .addInterceptor { chain ->
            val userAgentRequest = chain.request()
                .newBuilder()
                .header("User-Agent", userAgent)
                .build()
            return@addInterceptor chain.proceed(userAgentRequest)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
}

var stawalletApiClient = Retrofit.Builder()
    .baseUrl(STAWALLET_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(
        GsonConverterFactory.create(
            GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").create()
        )
    )
    .client(okHttpClient)
    .build()
    .create(StawalletV1ApiClient::class.java)


fun HttpException.verboseLocalizedMessage() = StringBuilder()
    .append(application.getString(R.string.reason_colon))
    .append(" ")
    .append(
        response()?.headers()?.get("X-Reason")?.snakeToHuman()
            ?: application.getString(R.string.unknown)
    )

    .append("\n")
    .append(application.getString(R.string.status_code_colon))
    .append(" ")
    .append(this.code())

    .append("\n")
    .append(application.getString(R.string.error_message_colon))
    .append(" ")
    .append(response()?.message() ?: application.getString(R.string.unknown))
    // .append("\n").append("Body: ")
    // .append(
    //     response().errorBody()?.string()?.run {
    //         if (length > 50) take(20) + " ... " + takeLast(20) else this
    //     } ?: "Unknown"
    // )
    .toString()

fun String.snakeToHuman() = if (isNullOrEmpty()) "" else this
    .replaceRange(0, 1, this[0].toUpperCase().toString())
    .replace("-", " ")
    .replace("([A-Za-z0-9]+)-".toRegex(), "$1")

fun HttpException.reason() = response()?.headers()?.get("X-Reason")