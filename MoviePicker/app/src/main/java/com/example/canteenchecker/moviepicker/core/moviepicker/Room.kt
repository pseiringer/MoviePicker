package com.example.canteenchecker.moviepicker.core.moviepicker

data class Room(
    val roomCode: String,
    val isClosed: Boolean,
    val votes: List<VoteSum>
)
