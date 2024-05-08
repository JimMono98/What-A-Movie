package com.jimmono.whatamovie.main.data.remote.dto

import com.jimmono.whatamovie.main.domain.models.Genre

data class GenresListDto(
    val genres: List<Genre>
)