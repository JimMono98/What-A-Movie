package com.jimmono.whatamovie.util.ui_shared_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jimmono.whatamovie.ui.theme.font

@Composable
fun DontHaveAccountRow(
    onSignupTap: () -> Unit = {},
) {
    Row(
        modifier = Modifier.padding(top=12.dp, bottom = 52.dp)
    ){
        Text("Don't have an account? ",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = font,
                color = Color.White
            )
        )

        Text("Sign Up",
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = font,
                fontWeight = FontWeight(800),
                color = Color.White
            ),
            modifier = Modifier.clickable {
                onSignupTap()
            }
        )


    }
}