package com.jimmono.whatamovie.main.data.local.media

import androidx.room.Database
import androidx.room.RoomDatabase

// Creating our local database - Room
//Noting Null cuz its our local database.


@Database(
    entities = [MediaEntity::class],
    version = 1
)
abstract class MediaDatabase: RoomDatabase() {
    abstract val mediaDao: MediaDao
}