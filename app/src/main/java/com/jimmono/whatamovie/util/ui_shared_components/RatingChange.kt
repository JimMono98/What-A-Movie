package com.jimmono.whatamovie.util.ui_shared_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RatingChange(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starSize: Dp = 24.dp,
    spaceBetween: Dp = 4.dp
) {
    Row(modifier = modifier) {
        for (i in 1..stars) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i <= rating) Color.Yellow else Color.Gray,
                modifier = Modifier
                    .size(starSize)
                    .clickable {
                        onRatingChange(i.toFloat())
                    }
                    .padding(end = spaceBetween)
            )
        }
    }
}