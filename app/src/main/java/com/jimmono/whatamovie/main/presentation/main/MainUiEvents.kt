package com.jimmono.whatamovie.main.presentation.main

// which we try to refresh or paginate -> popular... tvseries ...

sealed class MainUiEvents {
    data class Refresh(val type: String) : MainUiEvents()
    data class OnPaginate(val type: String) : MainUiEvents()
}