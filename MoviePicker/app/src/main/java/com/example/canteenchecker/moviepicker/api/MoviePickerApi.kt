package com.example.canteenchecker.moviepicker.api

import android.graphics.Bitmap
import com.example.canteenchecker.moviepicker.core.moviepicker.Room
import com.example.canteenchecker.moviepicker.core.moviepicker.Vote
import com.example.canteenchecker.moviepicker.core.tmdb.ConfigurationDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieDetails
import com.example.canteenchecker.moviepicker.core.tmdb.MovieSearchResult

interface MoviePickerApi {
    suspend fun getRoom(roomCode: String): Result<Room>
    suspend fun closeRoom(roomCode: String): Result<Unit>
    suspend fun deleteRoom(roomCode: String): Result<Unit>
    suspend fun createRoom(): Result<Room>
    suspend fun vote(roomCode: String, vote: Vote): Result<Unit>
}