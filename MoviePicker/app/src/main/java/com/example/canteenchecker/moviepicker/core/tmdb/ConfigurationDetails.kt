package com.example.canteenchecker.moviepicker.core.tmdb

data class ConfigurationDetails(
    val images: ConfigurationDetailsImages,
    val change_keys: List<String>,
)

data class ConfigurationDetailsImages(
    val base_url: String,
    val secure_base_url: String,
    val backdrop_sizes: List<String>,
    val logo_sizes: List<String>,
    val poster_sizes: List<String>,
    val profile_sizes: List<String>,
    val still_sizes: List<String>,
)
