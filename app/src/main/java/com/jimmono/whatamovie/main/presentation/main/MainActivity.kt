package com.jimmono.whatamovie.main.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.jimmono.whatamovie.media_details.presentation.details.MediaDetailsScreenEvents
import com.jimmono.whatamovie.media_details.presentation.details.MediaDetailsViewModel
import com.jimmono.whatamovie.media_details.presentation.details.MediaDetailScreen
import com.jimmono.whatamovie.media_details.presentation.details.SomethingWentWrong
import com.jimmono.whatamovie.media_details.presentation.similar_media.SimilarMediaListScreen
import com.jimmono.whatamovie.media_details.presentation.watch_video.WatchVideoScreen
import com.jimmono.whatamovie.search.presentation.SearchScreen
import com.jimmono.whatamovie.ui.theme.TheMoviesTheme
import com.jimmono.whatamovie.util.Route
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            NotificationPermission(this).displayNotification()
            TheMoviesTheme {

                val mainViewModel = hiltViewModel<MainViewModel>()
                val mainUiState = mainViewModel.mainUiState.collectAsState().value

                installSplashScreen().apply {
                    setKeepOnScreenCondition {
                        mainViewModel.showSplashScreen.value
                    }
                }

                Navigation(
                    mainUiState = mainUiState,
                    onEvent = mainViewModel::onEvent
                )
            }
        }

    }


}
@Composable
fun Navigation(
    mainUiState: MainUiState,
    onEvent: (MainUiEvents) -> Unit
) {
    val navController = rememberNavController()


    val mediaDetailsViewModel = hiltViewModel<MediaDetailsViewModel>()
    val mediaDetailsScreenState =
        mediaDetailsViewModel.mediaDetailsScreenState.collectAsState().value


    NavHost(
        navController = navController,
        startDestination = Route.WELCOME_SCREEN
    ) {

        composable(Route.MEDIA_MAIN_SCREEN) {
            MediaMainScreen(
                navController = navController,
                mainUiState = mainUiState,
                onEvent = onEvent
            )
        }

        composable(Route.SEARCH_SCREEN) {
            SearchScreen(
                navController = navController,
                mainUiState = mainUiState,
            )
        }
        composable(Route.LOGIN_SCREEN){
            LoginScreen(navController = navController, mainUiState = MainUiState()) {}
        }
        composable(Route.WELCOME_SCREEN) {
            WelcomeScreen(navController = navController, mainUiState = MainUiState()) {}
        }
        composable(Route.SIGNUP_SCREEN){
            SignupScreen(navController = navController, mainUiState = MainUiState()) {}
        }
        composable(Route.EDIT_PROFILE){
            EditProfile(navController = navController, mainUiState = MainUiState()) {}
        }


        composable(
            "${Route.MEDIA_DETAILS_SCREEN}?id={id}&type={type}&category={category}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("type") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) {

            val id = it.arguments?.getInt("id") ?: 0
            val type = it.arguments?.getString("type") ?: ""
            val category = it.arguments?.getString("category") ?: ""

            LaunchedEffect(key1 = true) {
                mediaDetailsViewModel.onEvent(
                    MediaDetailsScreenEvents.SetDataAndLoad(
                        moviesGenresList = mainUiState.moviesGenresList,
                        tvGenresList = mainUiState.tvGenresList,
                        id = id,
                        type = type,
                        category = category
                    )
                )
            }

            if (mediaDetailsScreenState.media != null) {
                MediaDetailScreen(
                    navController = navController,
                    media = mediaDetailsScreenState.media,
                    mediaDetailsScreenState = mediaDetailsScreenState,
                    onEvent = mediaDetailsViewModel::onEvent
                )
            } else {
                SomethingWentWrong()
            }
        }

        composable(
            "${Route.SIMILAR_MEDIA_LIST_SCREEN}?title={title}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
            )
        ) {

            val name = it.arguments?.getString("title") ?: ""

            SimilarMediaListScreen(
                navController = navController,
                mediaDetailsScreenState = mediaDetailsScreenState,
                name = name,
            )
        }

        composable(
            "${Route.WATCH_VIDEO_SCREEN}?videoId={videoId}",
            arguments = listOf(
                navArgument("videoId") { type = NavType.StringType }
            )

        ) {

            val videoId = it.arguments?.getString("videoId") ?: ""

            WatchVideoScreen(
                lifecycleOwner = LocalLifecycleOwner.current,
                videoId = videoId
            )
        }
    }
}












