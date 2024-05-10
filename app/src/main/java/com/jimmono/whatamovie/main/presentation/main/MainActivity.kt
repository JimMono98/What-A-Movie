package com.jimmono.whatamovie.main.presentation.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jimmono.whatamovie.R
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
    private val channelId = "What A Movie"
    private val notificationId = 1001
    private val REQUEST_CODE_PERMISSIONS = 101 // Choose a unique request code


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
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

                if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestNotificationPermission()
                } else {
                    displayNotification() // Display notification if permission granted
                }

            }
        }

    }

    fun requestNotificationPermission() {
        val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantResults ->
            val allPermissionsGranted = grantResults.values.all { it }
            if (allPermissionsGranted) {
                displayNotification()
            } else {
                // Handle case where permission is denied (e.g., show a message)
            }
        }
        activityResultLauncher.launch(permissions)
    }

    fun displayNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel() // Ensure channel is created before displaying notification
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Hi from What A Movie!")
            .setContentText("Just A Normal Notification")
            .setSmallIcon(R.drawable.ic_series)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            } else {
                requestNotificationPermission()
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "My Notification Channel" // Replace with a user-friendly name
        val descriptionText = "Channel description" // Optional description
        val importance = NotificationManager.IMPORTANCE_DEFAULT // Adjust importance as needed

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
        startDestination = Route.MEDIA_MAIN_SCREEN
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












