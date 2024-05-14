package com.jimmono.whatamovie.util.ui_shared_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.jimmono.whatamovie.main.presentation.main.MainUiEvents
import com.jimmono.whatamovie.ui.theme.font


@Composable
fun CButton(
    text: String,
    onClick: () -> Unit = {},

) {
    // make this button also resuable
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7C9A92)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {

        Text(
            text = text,
            style = TextStyle(
                fontSize = 22.sp,
                fontFamily = font,
                fontWeight = FontWeight(500),
                color = Color.White
            )
        )

    }
}