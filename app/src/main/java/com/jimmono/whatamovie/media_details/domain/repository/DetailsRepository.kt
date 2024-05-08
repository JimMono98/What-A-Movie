package com.jimmono.whatamovie.media_details.domain.repository

import com.jimmono.whatamovie.main.domain.models.Media
import com.jimmono.whatamovie.util.Resource
import kotlinx.coroutines.flow.Flow

interface DetailsRepository {

    suspend fun getDetails(
        type: String,
        isRefresh: Boolean,
        id: Int,
        apiKey: String
    ): Flow<Resource<Media>>

}










