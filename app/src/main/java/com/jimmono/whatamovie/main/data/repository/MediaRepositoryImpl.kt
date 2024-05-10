package com.jimmono.whatamovie.main.data.repository

import com.jimmono.whatamovie.main.data.local.media.MediaDatabase
import com.jimmono.whatamovie.main.data.mappers.toMedia
import com.jimmono.whatamovie.main.data.mappers.toMediaEntity
import com.jimmono.whatamovie.main.data.remote.api.MediaApi
import com.jimmono.whatamovie.main.domain.models.Media
import com.jimmono.whatamovie.main.domain.repository.MediaRepository
import com.jimmono.whatamovie.util.Constants
import com.jimmono.whatamovie.util.Constants.TRENDING
import com.jimmono.whatamovie.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Dagger Hilt - Dependency injection
// Flow -> Sequence of actions
// Data Layer for the App. from local database to get answers from api.

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val mediaApi: MediaApi,
    mediaDb: MediaDatabase
) : MediaRepository {

    private val mediaDao = mediaDb.mediaDao

    override suspend fun insertItem(media: Media) {
        val mediaEntity = media.toMediaEntity()

        mediaDao.insertMediaItem(
            mediaItem = mediaEntity
        )
    }

    override suspend fun getItem(
        id: Int,
        type: String,
        category: String,
    ): Media {
        return mediaDao.getMediaById(id).toMedia(
            category = category,
            type = type
        )
    }

    override suspend fun updateItem(media: Media) {
        val mediaEntity = media.toMediaEntity()

        mediaDao.updateMediaItem(
            mediaItem = mediaEntity
        )

    }

    override suspend fun getMoviesAndTvSeriesList(
        fetchFromRemote: Boolean,
        isRefresh: Boolean,
        type: String,
        category: String,
        page: Int,
        apiKey: String
    ): Flow<Resource<List<Media>>> {
        return flow {



            emit(Resource.Loading(true))

            val localMediaList = mediaDao.getMediaListByTypeAndCategory(type, category)

            // load the movies from database first
            // in case is not empty or we dont forcerefresh or fetch so we return a list of movies
            // if thats false we get results from api with some exceptions.
            val shouldJustLoadFromCache =
                localMediaList.isNotEmpty() && !fetchFromRemote && !isRefresh
            if (shouldJustLoadFromCache) {

                emit(Resource.Success(
                    data = localMediaList.map {
                        it.toMedia(
                            type = type,
                            category = category
                        )
                    }
                ))

                emit(Resource.Loading(false))
                return@flow
            }

            var searchPage = page
            if (isRefresh) {
                mediaDao.deleteMediaByTypeAndCategory(type, category)
                searchPage = 1
            }

            //parsing the type,cate... to the api call
            val remoteMediaList = try {
                mediaApi.getMoviesAndTvSeriesList(
                    type, category, searchPage, apiKey
                ).results
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }


            remoteMediaList.let { mediaList ->
                val media = mediaList.map {
                    it.toMedia(
                        type = type,
                        category = category
                    )
                }

                val entities = mediaList.map {
                    it.toMediaEntity(
                        type = type,
                        category = category,
                    )
                }

                mediaDao.insertMediaList(entities)

                emit(
                    Resource.Success(data = media)
                )
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getTrendingList(
        fetchFromRemote: Boolean,
        isRefresh: Boolean,
        type: String,
        time: String,
        page: Int,
        apiKey: String
    ): Flow<Resource<List<Media>>> {
        return flow {

            emit(Resource.Loading(true))

            val localMediaList = mediaDao.getTrendingMediaList(TRENDING)

            val shouldJustLoadFromCache = localMediaList.isNotEmpty() && !fetchFromRemote
            if (shouldJustLoadFromCache) {

                emit(Resource.Success(
                    data = localMediaList.map {
                        it.toMedia(
                            type = it.mediaType ?: Constants.unavailable,
                            category = TRENDING
                        )
                    }
                ))

                emit(Resource.Loading(false))
                return@flow
            }

            var searchPage = page

            if (isRefresh) {
                mediaDao.deleteTrendingMediaList(TRENDING)
                searchPage = 1
            }

            val remoteMediaList = try {
                mediaApi.getTrendingList(
                    type, time, searchPage, apiKey
                ).results
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                emit(Resource.Loading(false))
                return@flow
            }

            remoteMediaList.let { mediaList ->

                val media = mediaList.map {
                    it.toMedia(
                        type = it.media_type ?: Constants.unavailable,
                        category = TRENDING
                    )
                }

                val entities = mediaList.map {
                    it.toMediaEntity(
                        type = it.media_type ?: Constants.unavailable,
                        category = TRENDING
                    )
                }

                mediaDao.insertMediaList(entities)

                emit(
                    Resource.Success(data = media)
                )
                emit(Resource.Loading(false))
            }
        }
    }

}










