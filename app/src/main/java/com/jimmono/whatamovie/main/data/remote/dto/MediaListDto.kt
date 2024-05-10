package com.jimmono.whatamovie.main.data.remote.dto

// Data Transfare Object
//Jason To Kotlin Class

data class MediaListDto(
    val page: Int,
    val results: List<MediaDto>,
    val total_pages: Int,
    val total_results: Int
)