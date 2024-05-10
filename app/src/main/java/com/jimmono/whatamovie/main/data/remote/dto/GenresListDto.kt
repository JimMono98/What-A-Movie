package com.jimmono.whatamovie.main.data.remote.dto

import com.jimmono.whatamovie.main.domain.models.Genre

// Data Transfare Object
//Jason To Kotlin Class
//No Problem to get some null answers.

data class GenresListDto(
    val genres: List<Genre>
)