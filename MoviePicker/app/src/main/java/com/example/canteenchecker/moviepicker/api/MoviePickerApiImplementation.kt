package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import android.util.Log
import com.example.canteenchecker.moviepicker.BuildConfig
import com.example.canteenchecker.moviepicker.core.moviepicker.Room
import com.example.canteenchecker.moviepicker.core.moviepicker.Vote
import com.example.canteenchecker.moviepicker.core.moviepicker.VoteList
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.io.IOException


object MoviePickerApiFactory {
    fun createMoviePickerApi(): MoviePickerApi =
        MoviePickerApiImplementation()
}

private class MoviePickerApiImplementation () : MoviePickerApi {
    var client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .build()
        chain.proceed(newRequest)
    }.build()

    private val retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BackendBaseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    override suspend fun getRoom(roomCode: String): Result<Room> = apiCall {
        getRoom(roomCode)
    }

    override suspend fun closeRoom(roomCode: String): Result<Unit> = apiCall {
        closeRoom(roomCode)
    }

    override suspend fun deleteRoom(roomCode: String): Result<Unit> = apiCall {
        deleteRoom(roomCode)
    }

    override suspend fun createRoom(): Result<Room> = apiCall {
        createRoom()
    }

    override suspend fun vote(roomCode: String, vote: Vote): Result<Unit> = apiCall {
        vote(roomCode, vote)
    }

    override suspend fun voteList(roomCode: String, voteList: VoteList): Result<Unit> = apiCall {
        voteList(roomCode, voteList)
    }


    private interface Api {

        @GET("Rooms/{roomCode}")
        suspend fun getRoom(@Path("roomCode") roomCode: String): Room

        @PUT("Rooms/{roomCode}")
        suspend fun closeRoom(@Path("roomCode") roomCode: String): Response<Unit>

        @DELETE("Rooms/{roomCode}")
        suspend fun deleteRoom(@Path("roomCode") roomCode: String): Response<Unit>

        @POST("Rooms")
        suspend fun createRoom(): Room

        @POST("Rooms/{roomCode}/Votes")
        suspend fun vote(@Path("roomCode") roomCode: String, @Body vote: Vote): Response<Unit>

        @POST("Rooms/{roomCode}/VoteList")
        suspend fun voteList(@Path("roomCode") roomCode: String, @Body voteList: VoteList): Response<Unit>

    }

    private inline fun <T> apiCall(call: Api.() -> T): Result<T> = try {
        Result.success(call(retrofit.create()))
    } catch (ex: HttpException) {
        Result.failure(ex)
    } catch (ex: IOException) {
        Result.failure(ex)
    }.onFailure {
        Log.e(TAG, "API call failed", it)
    }

    companion object {
        private val TAG = this::class.simpleName
    }

}

private inline fun <T, R> Result<List<T>>.convertEach(map: T.() -> R): Result<List<R>> =
    this.map { it.map(map) }

private inline fun <T, R> Result<T>.convert(map: T.() -> R): Result<R> = this.map(map)