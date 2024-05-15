package com.jimmono.whatamovie.media_details.presentation.details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.main.data.remote.api.MediaApi
import com.jimmono.whatamovie.main.data.remote.firestore.Review
import com.jimmono.whatamovie.main.domain.models.Media
import com.jimmono.whatamovie.media_details.presentation.details.detailScreenUiComponents.MovieImage
import com.jimmono.whatamovie.theme.SmallRadius
import com.jimmono.whatamovie.ui.theme.font
import com.jimmono.whatamovie.util.Constants
import com.jimmono.whatamovie.util.Route
import com.jimmono.whatamovie.util.ui_shared_components.Item
import com.jimmono.whatamovie.util.ui_shared_components.RatingBar
import com.jimmono.whatamovie.util.ui_shared_components.RatingChange
import com.jimmono.whatamovie.util.ui_shared_components.genresProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MediaDetailScreen(
    navController: NavController,
    media: Media,
    mediaDetailsScreenState: MediaDetailsScreenState,
    onEvent: (MediaDetailsScreenEvents) -> Unit
) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        onEvent(MediaDetailsScreenEvents.Refresh)
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)

    val imageUrl = "${MediaApi.IMAGE_BASE_URL}${media.backdropPath}"

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(Size.ORIGINAL)
            .build()
    )
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var isReviewSectionVisible by remember { mutableStateOf(true) }
    var reviews by remember { mutableStateOf<List<Review>?>(null) }

    fun fetchReviews(movieTitle: String) {
        FirebaseFirestore.getInstance().collection("reviews")
            .whereEqualTo("movieTitle", movieTitle)
            .get()
            .addOnSuccessListener { result ->
                val fetchedReviews = result.toObjects(Review::class.java)
                reviews = fetchedReviews
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun submitReview(movieTitle: String, rating: Float, comment: String) {
        if (currentUser != null) {
            val review = Review(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anonymous",
                movieTitle = movieTitle,
                rating = rating,
                comment = comment
            )
            coroutineScope.launch {
                FirebaseFirestore.getInstance().collection("reviews")
                    .add(review)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Review submitted", Toast.LENGTH_SHORT).show()
                        isReviewSectionVisible = false
                        fetchReviews(movieTitle) // Fetch reviews again after submission
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to submit review: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(context, "Please log in to submit a review", Toast.LENGTH_SHORT).show()
        }
    }




    val surface = MaterialTheme.colorScheme.surface
    var averageColor by remember {
        mutableStateOf(surface)
    }

    LaunchedEffect(key1 = media.title) {
        fetchReviews(media.title)
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pullRefresh(refreshState)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                VideoSection(
                    navController = navController,
                    mediaDetailsScreenState = mediaDetailsScreenState,
                    media = media,
                    imageState = imagePainter.state,
                    onEvent = onEvent
                ) { color ->
                    averageColor = color
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {

                    PosterSection(media = media) {}

                    Spacer(modifier = Modifier.width(12.dp))

                    InfoSection(
                        media = media,
                        mediaDetailsScreenState = mediaDetailsScreenState
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            OverviewSection(media = media)

            SimilarMediaSection(
                navController = navController,
                media = media,
                mediaDetailsScreenState = mediaDetailsScreenState
            )

            Spacer(modifier = Modifier.height(16.dp))

            Rating(media = media)

            if (isReviewSectionVisible) {
                ReviewSection(media = media) { rating, comment ->
                    submitReview(media.title, rating, comment)
                }
            }

            reviews?.let {
                if (it.isNotEmpty()) {
                    ReviewList(reviews = it)
                }
            }
        }

        PullRefreshIndicator(
            refreshing, refreshState, Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun VideoSection(
    navController: NavController,
    mediaDetailsScreenState: MediaDetailsScreenState,
    media: Media,
    imageState: AsyncImagePainter.State,
    onEvent: (MediaDetailsScreenEvents) -> Unit,
    onImageLoaded: (color: Color) -> Unit
) {

    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable {

                if (mediaDetailsScreenState.videosList.isNotEmpty()) {
                    onEvent(MediaDetailsScreenEvents.NavigateToWatchVideo)
                    navController.navigate(
                        "${Route.WATCH_VIDEO_SCREEN}?videoId=${mediaDetailsScreenState.videoId}"
                    )
                } else {
                    Toast
                        .makeText(
                            context,
                            context.getString(R.string.no_video_is_available_at_the_moment),
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }

            },
        shape = RoundedCornerShape(0),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {

            MovieImage(
                imageState = imageState,
                description = media.title,
                noImageId = null,
            ) { color ->
                onImageLoaded(color)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .size(50.dp)
                    .alpha(0.7f)
                    .background(Color.LightGray)
            )
            Icon(
                Icons.Rounded.PlayArrow,
                contentDescription = stringResource(id = R.string.watch_trailer),
                tint = Color.Black,
                modifier = Modifier.size(35.dp)
            )

        }
    }
}

@Composable
fun PosterSection(
    media: Media,
    onImageLoaded: (color: Color) -> Unit
) {

    val posterUrl = "${MediaApi.IMAGE_BASE_URL}${media.posterPath}"
    val posterPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(posterUrl).size(Size.ORIGINAL)
            .build()
    )
    val posterState = posterPainter.state


    Column {
        Spacer(modifier = Modifier.height(200.dp))

        Card(
            modifier = Modifier
                .width(180.dp)
                .height(250.dp)
                .padding(start = 16.dp),
            shape = RoundedCornerShape(SmallRadius),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                MovieImage(
                    imageState = posterState,
                    description = media.title,
                    noImageId = Icons.Rounded.ImageNotSupported
                ) { color ->
                    onImageLoaded(color)
                }
            }
        }
    }
}

@Composable
fun InfoSection(
    media: Media,
    mediaDetailsScreenState: MediaDetailsScreenState,
) {

    val genres = genresProvider(
        genre_ids = media.genreIds,
        allGenres = if (media.mediaType == Constants.MOVIE) mediaDetailsScreenState.moviesGenresList
        else mediaDetailsScreenState.tvGenresList
    )

    Column {
        Spacer(modifier = Modifier.height(260.dp))

        Text(
            text = media.title,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            fontSize = 19.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingBar(
                modifier = Modifier,
                starsModifier = Modifier.size(18.dp),
                rating = media.voteAverage.div(2)
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = 4.dp
                ),
                text = media.voteAverage.toString().take(3),
                fontFamily = font,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {


            Text(
                text =
                if (media.releaseDate != Constants.unavailable)
                    media.releaseDate.take(4)
                else "",

                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = font,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 0.5.dp),
                text = if (media.adult) "+18" else "-12",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = font,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = genres,
            fontFamily = font,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = mediaDetailsScreenState.readableTime,
            fontFamily = font,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )
    }
}


@Composable
fun OverviewSection(
    media: Media
) {
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = "\"${media.tagline ?: ""}\"",
            fontFamily = font,
            fontSize = 17.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = stringResource(R.string.overview),
            fontFamily = font,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = media.overview,
            fontFamily = font,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )

    }
}

@Composable
fun SimilarMediaSection(
    navController: NavController,
    media: Media,
    mediaDetailsScreenState: MediaDetailsScreenState,
) {

    val title = stringResource(id = R.string.similar)
    val mediaList = mediaDetailsScreenState.smallSimilarMediaList

    if (mediaList.isNotEmpty()) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 22.dp, top = 22.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = font,
                    fontSize = 18.sp
                )

                Text(
                    modifier = Modifier
                        .alpha(0.85f)
                        .clickable {
                            navController.navigate(
                                "${Route.SIMILAR_MEDIA_LIST_SCREEN}?title=${media.title}"
                            )
                        },
                    text = stringResource(id = R.string.see_all),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = font,
                    fontSize = 14.sp,
                )
            }

            LazyRow {
                items(mediaList.size) {

                    var paddingEnd = 0.dp
                    if (it == mediaList.size - 1) {
                        paddingEnd = 16.dp
                    }

                    Item(
                        media = mediaList[it],
                        navController = navController,
                        modifier = Modifier
                            .height(200.dp)
                            .width(150.dp)
                            .padding(start = 16.dp, end = paddingEnd)
                    )
                }
            }
        }
    }
}
@Composable
fun Rating(
    media: Media
) {
    Column {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.padding(horizontal = 22.dp),
            text = stringResource(R.string.rating),
            fontFamily = font,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
        )
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 4.dp
                ),
                text = "Average Rating from IMDb :",
                fontFamily = font,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                modifier = Modifier.padding(
                    horizontal = 4.dp
                ),
                text = media.voteAverage.toString().take(3),
                fontFamily = font,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )

            RatingBar(
                modifier = Modifier,
                starsModifier = Modifier.size(30.dp),
                rating = media.voteAverage.div(2)
            )

        }

    }
}

@Composable
fun ReviewSection(
    media: Media,
    onSubmit: (rating: Float, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text("Submit your review for ${media.title}", color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(8.dp))

        RatingChange(
            rating = rating,
            onRatingChange = { newRating ->
                rating = newRating
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comment") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onSubmit(rating, comment) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }
    }
}
@Composable
fun ReviewList(reviews: List<Review>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Reviews", color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.height(8.dp))

        reviews.forEach { review ->
            ReviewItem(review = review)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "User: ${review.userName}", fontWeight = FontWeight.Bold)
        Text(text = "Rating: ${review.rating}")
        Text(text = review.comment)
    }
}
@Composable
fun SomethingWentWrong() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font,
            fontSize = 19.sp
        )
    }
}



