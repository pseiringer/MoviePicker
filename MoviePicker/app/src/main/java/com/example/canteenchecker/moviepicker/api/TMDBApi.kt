package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import com.example.canteenchecker.moviepicker.core.tmdb.ConfigurationDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieSearchResult

interface TMDBApi {
    suspend fun getConfigurationDetails(): Result<ConfigurationDetails>
    suspend fun getImage(baseUrl: String, fileSize: String, filePath: String): Result<Bitmap>
    // https://www.geeksforgeeks.org/download-image-from-url-in-android/
    suspend fun searchMovie(
        query: String,
        includeAdult: Boolean? = null,
        language: String? = null,
        primaryReleaseYear: String? = null,
        page: Int? = null,
        region: String? = null,
        year: String? = null): Result<MovieSearchResult>
    suspend fun getMovie(movieId: Int, language: String? = null): Result<MovieDetails>
}