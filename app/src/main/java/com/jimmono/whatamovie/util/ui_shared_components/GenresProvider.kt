package com.jimmono.whatamovie.util.ui_shared_components

import androidx.compose.runtime.Composable
import com.jimmono.whatamovie.main.domain.models.Genre

@Composable
fun genresProvider(
    genre_ids: List<Int>,
    allGenres: List<Genre>
): String {


    var genres = ""

    for (i in allGenres.indices) {
        for (j in genre_ids.indices) {
            if (allGenres[i].id == genre_ids[j]) {
                genres += "${allGenres[i].name} - "
            }
        }
    }

    return genres.dropLastWhile { it == ' ' || it == '-' }
}













