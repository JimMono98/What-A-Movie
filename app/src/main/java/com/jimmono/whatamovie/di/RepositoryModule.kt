package com.jimmono.whatamovie.di

import com.jimmono.whatamovie.media_details.data.repository.DetailsRepositoryImpl
import com.jimmono.whatamovie.media_details.data.repository.ExtraDetailsRepositoryImpl
import com.jimmono.whatamovie.media_details.domain.repository.DetailsRepository
import com.jimmono.whatamovie.media_details.domain.repository.ExtraDetailsRepository
import com.jimmono.whatamovie.main.data.repository.GenreRepositoryImpl
import com.jimmono.whatamovie.main.data.repository.MediaRepositoryImpl
import com.jimmono.whatamovie.search.data.repository.SearchRepositoryImpl
import com.jimmono.whatamovie.main.domain.repository.GenreRepository
import com.jimmono.whatamovie.main.domain.repository.MediaRepository
import com.jimmono.whatamovie.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Dependency Injection package.

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    //  Binds the interface to the implementation.

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    @Singleton
    abstract fun bindGenreRepository(
        genreRepositoryImpl: GenreRepositoryImpl
    ): GenreRepository

    @Binds
    @Singleton
    abstract fun bindDetailsRepository(
        detailsRepositoryImpl: DetailsRepositoryImpl
    ): DetailsRepository

    @Binds
    @Singleton
    abstract fun bindExtraDetailsRepository(
       extraDetailsRepositoryImpl: ExtraDetailsRepositoryImpl
    ): ExtraDetailsRepository

}
