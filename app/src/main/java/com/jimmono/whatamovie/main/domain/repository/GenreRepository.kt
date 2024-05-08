package com.jimmono.whatamovie.main.domain.repository

import com.jimmono.whatamovie.util.Resource
import com.jimmono.whatamovie.main.domain.models.Genre
import kotlinx.coroutines.flow.Flow

interface GenreRepository {
    suspend fun getGenres(
        fetchFromRemote: Boolean,
        type: String,
        apiKey: String
    ): Flow<Resource<List<Genre>>>
}










