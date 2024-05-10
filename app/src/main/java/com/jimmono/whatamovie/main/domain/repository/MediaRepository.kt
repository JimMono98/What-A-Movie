package com.jimmono.whatamovie.main.domain.repository

import com.jimmono.whatamovie.util.Resource
import com.jimmono.whatamovie.main.domain.models.Media
import kotlinx.coroutines.flow.Flow

// Flow -> Sequence of actions

interface MediaRepository {

    suspend fun updateItem(media: Media)

    suspend fun insertItem(media: Media)

    suspend fun getItem(
        id: Int,
        type: String,
        category: String
    ): Media

    suspend fun getMoviesAndTvSeriesList(
        //force fetch so we can move down and get more movies
        fetchFromRemote: Boolean,
        isRefresh: Boolean,
        type: String,
        category: String,
        page: Int,
        apiKey: String
    ): Flow<Resource<List<Media>>>

    suspend fun getTrendingList(
        fetchFromRemote: Boolean,
        isRefresh: Boolean,
        type: String,
        time: String,
        page: Int,
        apiKey: String
    ): Flow<Resource<List<Media>>>

}










