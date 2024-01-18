package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.canteenchecker.moviepicker.BuildConfig
import com.example.canteenchecker.moviepicker.core.tmdb.ConfigurationDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieSearchResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream


object TMDBApiFactory {
    fun createTMDBApi(): TMDBApi =
        TMDBApiImplementation()
}

private class TMDBApiImplementation () : TMDBApi {
    var client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.TMDBToken}")
            .build()
        chain.proceed(newRequest)
    }.build()

    private val retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.TMDBBaseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    override suspend fun getConfigurationDetails(): Result<ConfigurationDetails> = apiCall {
        getConfigurationDetails()
    }

    override suspend fun getImage(
        baseUrl: String,
        fileSize: String,
        filePath: String
    ): Result<Bitmap>  = apiCall {
//            getImage("http://image.tmdb.org/t/p/original/gq5Wi7i4SF3lo4HHkJasDV95xI9.jpg")
            getImage("$baseUrl$fileSize/$filePath")
        }.convert {
            BitmapFactory.decodeStream(BufferedInputStream(this.byteStream(), 1024 * 8))
        }

//            Result<Bitmap> { return Result.failure(NotImplementedError()); }


    override suspend fun searchMovie(
        query: String,
        includeAdult: Boolean?,
        language: String?,
        primaryReleaseYear: String?,
        page: Int?,
        region: String?,
        year: String?
    ): Result<MovieSearchResult> = apiCall {
        searchMovie(query, includeAdult, language, primaryReleaseYear, page, region, year)
    }

    override suspend fun getMovie(movieId: Int, language: String?): Result<MovieDetails> = apiCall {
        getMovie(movieId, language)
    }

    private interface Api {
        @GET("configuration")
        suspend fun getConfigurationDetails(): ConfigurationDetails

        @GET
        suspend fun getImage(@Url url: String): ResponseBody
//        // https://www.geeksforgeeks.org/download-image-from-url-in-android/
        @GET("search/movie")
        suspend fun searchMovie(
            @Query("query") query: String, @Query("includeAdult") includeAdult: Boolean?,
            @Query("language") language: String?, @Query("primaryReleaseYear") primaryReleaseYear: String?,
            @Query("page") page: Int?, @Query("region") region: String?,
            @Query("year") year: String?): MovieSearchResult
        @GET("movie/{movieId}")
        suspend fun getMovie(@Path("movieId") movieId: Int, @Query("language") language: String?): MovieDetails

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
//    private inline fun apiCallImage(call: Api.() -> Call<ResponseBody?>?): Call<ResponseBody?>? = try {
//        call(retrofit.create())
//    } catch (ex: HttpException) {
//        null
//    } catch (ex: IOException) {
//        null
//    }

    companion object {
        private val TAG = this::class.simpleName
    }

}

private inline fun <T, R> Result<List<T>>.convertEach(map: T.() -> R): Result<List<R>> =
    this.map { it.map(map) }

private inline fun <T, R> Result<T>.convert(map: T.() -> R): Result<R> = this.map(map)