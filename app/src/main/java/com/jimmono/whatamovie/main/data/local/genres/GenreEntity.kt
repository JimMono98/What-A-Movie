package com.jimmono.whatamovie.main.data.local.genres

import androidx.room.Entity
import androidx.room.PrimaryKey

//Storing on Database The differences.
//Noting Null cuz its our local database.

@Entity
data class GenreEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String
)