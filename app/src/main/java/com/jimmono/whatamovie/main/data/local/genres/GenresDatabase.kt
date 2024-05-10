package com.jimmono.whatamovie.main.data.local.genres

import androidx.room.Database
import androidx.room.RoomDatabase

// Creating our local database - Room
//Noting Null cuz its our local database.

@Database(
    entities = [GenreEntity::class],
    version = 1
)
abstract class GenresDatabase: RoomDatabase() {
    abstract val genreDao: GenreDao
}