package com.jimmono.whatamovie.main.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.util.ui_shared_components.CButton
import com.jimmono.whatamovie.util.ui_shared_components.DontHaveAccountRow
import com.jimmono.whatamovie.ui.theme.font
import com.jimmono.whatamovie.util.Route


@Composable
fun WelcomeScreen(
    navController: NavController,
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvents) -> Unit
) {

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        /// Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .width(320.dp)
                    .height(240.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                "Welcome to What a Movie!",
                fontSize = 28.sp,
                fontFamily = font,
                fontWeight = FontWeight(700),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                "Your go-to destination for insightful film reviews and recommendations!.",
                textAlign = TextAlign.Center,
                fontFamily = font,
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))


            CButton(text = "Sign In With Email",
                onClick = {
                        navController.navigate(Route.LOGIN_SCREEN)
                }
            )

            DontHaveAccountRow(
                onSignupTap = {
                    navController.navigate(Route.SIGNUP_SCREEN)
                }
            )



        }
    }

}
