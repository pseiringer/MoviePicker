package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import com.example.canteenchecker.moviepicker.core.tmdb.ConfigurationDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieSearchResult

interface TMDBApi {
    suspend fun getConfigurationDetails(filter: String): Result<List<ConfigurationDetails>>
    suspend fun getImage(baseUrl: String, fileSize: String, filePath: String): Result<Bitmap>
    // https://www.geeksforgeeks.org/download-image-from-url-in-android/
    suspend fun searchMovie(query: String, includeAdult: Boolean, language: String,
                            primaryReleaseYear: String, page: Int, region: String, year: String): Result<MovieSearchResult>
    suspend fun getMovie(movieId: Int): Result<MovieDetails>
}