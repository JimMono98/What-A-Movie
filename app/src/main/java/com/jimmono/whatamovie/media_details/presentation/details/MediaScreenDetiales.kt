package com.jimmono.whatamovie.media_details.presentation.details

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextField
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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.main.data.remote.api.MediaApi
import com.jimmono.whatamovie.main.data.remote.firestore.Review
import com.jimmono.whatamovie.main.data.remote.firestore.editReview
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
import com.jimmono.whatamovie.ui.theme.yellow
import timber.log.Timber


@RequiresApi(Build.VERSION_CODES.O)
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

    // State to track if the user has already commented
    var hasUserCommented by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()


    fun deleteReview(review: Review, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val reviewRef = db.collection("reviews").document(review.id)
        reviewRef.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Timber.tag("Firestore").e(e, "Error deleting review")
                onFailure(e)
            }
    }

    // Function to check if the user has commented on this media
    fun checkIfUserCommented(mediaTitle: String, userId: String) {
        FirebaseFirestore.getInstance().collection("reviews")
            .whereEqualTo("movieTitle", mediaTitle)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                hasUserCommented = !result.isEmpty
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to check comments: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

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
                id = "", // Temporary placeholder, will be updated with Firestore ID
                userId = currentUser.uid,
                userEmail = currentUser.email ?: "Anonymous",
                movieTitle = movieTitle,
                rating = rating,
                comment = comment
            )
            coroutineScope.launch {
                FirebaseFirestore.getInstance().collection("reviews")
                    .add(review)
                    .addOnSuccessListener { documentReference ->
                        val reviewId = documentReference.id
                        review.id = reviewId // Update review object with Firestore ID

                        // Now, update the review in Firestore with the correct ID
                        documentReference.set(review)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Review submitted", Toast.LENGTH_SHORT).show()
                                isReviewSectionVisible = false
                                fetchReviews(movieTitle) // Fetch reviews again after submission
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Failed to update review with ID: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
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
    var averageColor by remember { mutableStateOf(surface) }

    LaunchedEffect(key1 = media.title) {
        fetchReviews(media.title)
        currentUser?.let { user ->
            checkIfUserCommented(media.title, user.uid)
        }
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

            // Conditionally render the review section
            if (isReviewSectionVisible && !hasUserCommented) {
                ReviewSection(media = media) { rating, comment ->
                    submitReview(media.title, rating, comment)
                }
            } else if (hasUserCommented) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "You have already reviewed this Movie/Series.",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                            fontSize = 14.sp,
                        )
                    }
                }
            }

            reviews?.let {
                if (it.isNotEmpty()) {
                    ReviewList(
                        reviews = reviews!!,
                        currentUserEmail = currentUser?.email ?: "",
                        onEdit = { review ->

                        },
                        onDelete = { review ->
                            deleteReview(
                                review,
                                onSuccess = {
                                    Toast.makeText(context, "Review deleted successfully", Toast.LENGTH_SHORT).show()
                                            },
                                onFailure = {
                                    e -> Toast.makeText(context, "Error deleting review: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
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
    val maxLength = 40

    Surface(
        shape = MaterialTheme.shapes.large, // Use a predefined shape
        border = BorderStroke(1.dp, Color.Gray),
        color = MaterialTheme.colorScheme.surface, // Add a border
        modifier = Modifier
            .padding(8.dp) // Add padding around the border
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Submit your review for ${media.title}",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = font,
                fontSize = 16.sp
            )

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
                onValueChange = { newComment ->
                    if (newComment.length <= maxLength) {
                        comment = newComment
                    }
                },
                label = {
                    Text(
                        "Comment",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = font,
                        fontSize = 14.sp,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "${comment.length} / $maxLength",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = font,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSubmit(rating, comment) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    "Submit",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = font,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewList(
    reviews: List<Review>,
    currentUserEmail: String,
    onEdit: (Review) -> Unit,
    onDelete: (Review) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editedReview by remember { mutableStateOf<Review?>(null) }
    val context = LocalContext.current

    val sortedReviews = reviews.sortedByDescending { it.timestamp }  // Sort by timestamp descending

    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Reviews on What a Film!",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = font,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        sortedReviews.forEach { review ->
            ReviewItem(
                review = review,
                currentUserEmail = currentUserEmail,
                onEdit = { reviewToEdit ->
                    // Open edit dialog
                    showDialog = true
                    editedReview = reviewToEdit
                },
                onDelete = onDelete
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        editedReview?.let { review ->
            if (showDialog) {
                EditReviewDialog(
                    review = review,
                    onEditReview = { editedComment: String, rating: Float ->
                        // Call edit review function
                        editReview(review, editedComment, rating,
                            onSuccess = {
                                // Handle success
                                Toast.makeText(context, "Review edited successfully", Toast.LENGTH_SHORT).show()
                                fetchReviews(review.movieTitle,
                                    onComplete = { updatedReviews ->
                                        // Update the reviews state here
                                    },
                                    onError = { e ->
                                        // Handle error
                                        Toast.makeText(context, "Error fetching reviews: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            onFailure = { e ->
                                // Handle failure
                                Toast.makeText(context, "Error editing review: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                        showDialog = false
                    },
                    dismissDialog = { showDialog = false }
                )
            }
        }
    }
}

fun fetchReviews(movieTitle: String, onComplete: (List<Review>) -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("reviews")
        .whereEqualTo("movieTitle", movieTitle)
        .get()
        .addOnSuccessListener { result ->
            val reviews = result.map { document ->
                Review(
                    id = document.id,
                    movieTitle = document.getString("movieTitle") ?: "",
                    comment = document.getString("comment") ?: "",
                    userEmail = document.getString("userEmail") ?: ""
                )
            }
            onComplete(reviews)
        }
        .addOnFailureListener { e ->
            onError(e)
        }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewItem(
    review: Review,
    currentUserEmail: String,
    onEdit: (Review) -> Unit,
    onDelete: (Review) -> Unit
) {
    val rating = "${review.rating}"
    Surface(
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, Color.Gray),
        modifier = Modifier
            .height(150.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Email: ${review.userEmail}",
                fontFamily = font,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rating: ${review.rating}/5.0",
                    color = yellow,
                    fontFamily = font,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                RatingBar(
                    modifier = Modifier,
                    starsModifier = Modifier.size(12.dp),
                    rating = rating.toDouble()
                )

                Text(
                    text = review.timestamp.toDate().toString().substring(8, 11) +
                            review.timestamp.toDate().toString().substring(3, 8) +
                            review.timestamp.toInstant().toString().substring(0, 4) +
                            review.timestamp.toDate().toString().substring(10, 20),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = font,
                    fontSize = 12.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                fontFamily = font,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (review.userEmail == currentUserEmail) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Button(
                        onClick = { onEdit(review) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        modifier = Modifier
                            .height(50.dp)
                            .width(50.dp),
                        contentPadding = PaddingValues(0.dp) // Remove default padding
                    ) {
                        Text(
                            "Edit",
                            fontFamily = font,
                            fontSize = 10.sp, // Smaller text size
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Text(" ")
                    Button(
                        onClick = { onDelete(review) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        modifier = Modifier.size(50.dp),
                        contentPadding = PaddingValues(0.dp) // Remove default padding
                    ) {
                        Text(
                            "Delete",
                            fontFamily = font,
                            fontSize = 10.sp, // Smaller text size
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }

            }}
    }
}
@Composable
fun EditReviewDialog(
    review: Review,
    onEditReview: (String, Float) -> Unit, // Modify to include rating
    dismissDialog: () -> Unit
) {
    var editedComment by remember { mutableStateOf(review.comment) }
    var rating by remember { mutableFloatStateOf(review.rating) }

    AlertDialog(
        onDismissRequest = { dismissDialog() },
        title = { Text("Edit Review") },
        text = {
            Column {
                TextField(
                    value = editedComment,
                    onValueChange = { editedComment = it },
                    label = { Text("Enter your edited review") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingChange(
                    rating = rating,
                    onRatingChange = { newRating ->
                        rating = newRating
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onEditReview(editedComment, rating) // Pass rating along with comment
                    dismissDialog()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                onClick = { dismissDialog() }
            ) {
                Text("Cancel")
            }
        }
    )
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



