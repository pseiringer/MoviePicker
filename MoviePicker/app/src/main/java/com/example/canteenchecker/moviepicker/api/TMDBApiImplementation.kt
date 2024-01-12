package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import android.util.Log
import com.example.canteenchecker.moviepicker.core.tmdb.ConfigurationDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieSearchResult
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.io.IOException

object TMDBApiFactory {
    fun createTMDBApi(): TMDBApi =
        TMDBApiImplementation()
}

private class TMDBApiImplementation () : TMDBApi {
    private val retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    override suspend fun getConfigurationDetails(filter: String): Result<List<ConfigurationDetails>> {
        TODO("Not yet implemented")
    }

    override suspend fun getImage(
        baseUrl: String,
        fileSize: String,
        filePath: String
    ): Result<Bitmap> {
        TODO("Not yet implemented")
    }

    override suspend fun searchMovie(
        query: String,
        includeAdult: Boolean,
        language: String,
        primaryReleaseYear: String,
        page: Int,
        region: String,
        year: String
    ): Result<MovieSearchResult> {
        TODO("Not yet implemented")
    }

    override suspend fun getMovie(movieId: Int): Result<MovieDetails> {
        TODO("Not yet implemented")
    }

    private interface Api {
        @POST("authenticate")
        suspend fun postAuthenticate(
            @Query("userName") userName: String, @Query("password") password: String
        ): String

        @GET("canteens")
        suspend fun getCanteens(@Query("name") name: String): List<ApiCanteenData>

        @GET("canteens/{canteenId}")
        suspend fun getCanteen(@Path("canteenId") canteenId: String): ApiCanteenDetails

        @GET("canteens/{canteenId}/review-statistics")
        suspend fun getReviewStatisticsForCanteen(@Path("canteenId") canteenId: String): ApiCanteenReviewStatistics

        @POST("canteens/{canteenId}/reviews")
        suspend fun postCanteenReview(
            @Header("Authorization") authenticationToken: String,
            @Path("canteenId") canteenId: String,
            @Query("rating") rating: Int,
            @Query("remark") remark: String
        ) : Response<Unit>
    }

//    private class ApiCanteenData(
//        val id: String,
//        val name: String,
//        val dish: String,
//        val dishPrice: Float,
//        val averageRating: Float
//    )
//
//    private class ApiCanteenDetails(
//        val name: String,
//        val address: String,
//        val phoneNumber: String,
//        val website: String,
//        val dish: String,
//        val dishPrice: Float,
//        val waitingTime: Int
//    )
//
//    private class ApiCanteenReviewStatistics(
//        val countOneStar: Int,
//        val countTwoStars: Int,
//        val countThreeStars: Int,
//        val countFourStars: Int,
//        val countFiveStars: Int
//    )

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