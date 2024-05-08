package com.jimmono.whatamovie.search.domain.repository

import com.jimmono.whatamovie.util.Resource
import com.jimmono.whatamovie.main.domain.models.Media
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun getSearchList(
        fetchFromRemote: Boolean,
        query: String,
        page: Int,
        apiKey: String
    ): Flow<Resource<List<Media>>>

}










