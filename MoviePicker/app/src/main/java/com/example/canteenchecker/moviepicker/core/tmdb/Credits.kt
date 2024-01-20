package com.example.canteenchecker.moviepicker.core.tmdb

data class Credits(
    val id: Int,
    val cast: List<CastMember>,
    val crew: List<CrewMember>,
)

data class CastMember(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val knownForDepartment: String,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String,
    val castId: Int,
    val character: String,
    val creditId: String,
    val order: Int
)

data class CrewMember(
    val adult: Boolean,
    val gender: Int,
    val id: Int,
    val knownForDepartment: String,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String,
    val creditId: String,
    val department: String,
    val job: String
)