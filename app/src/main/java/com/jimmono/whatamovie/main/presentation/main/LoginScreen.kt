package com.jimmono.whatamovie.main.presentation.main

import android.content.ContentValues
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.util.ui_shared_components.CTextField
import com.jimmono.whatamovie.util.ui_shared_components.CButton
import com.jimmono.whatamovie.util.ui_shared_components.DontHaveAccountRow
import com.jimmono.whatamovie.ui.theme.font
import com.jimmono.whatamovie.util.Route
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvents) -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()


    Surface(
        color = Color(0xFF253334),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bg1),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .align(Alignment.BottomCenter)
            )

            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .height(100.dp)
                        .align(Alignment.Start)
                        .offset(x = (-20).dp)
                )

                Text(
                    text = "Sign In",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = font,
                        fontWeight = FontWeight(500),
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Sign In now to access your exercises and saved music.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = font,
                        color = Color(0xB2FFFFFF)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                // Text Fields
                CTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email Address"
                )

                CTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                CButton(
                    text = "Sign In",
                    onClick = {
                        // Perform Firebase authentication
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Timber.tag(ContentValues.TAG).d("signInWithEmail:success")
                                    navController.navigate(Route.MEDIA_MAIN_SCREEN)
                                } else {
                                    Timber.tag(ContentValues.TAG).w(task.exception, "signInWithCredential:failure")
                                }

                            }
                    }
                )

                // "Don't have an account?" Row
                DontHaveAccountRow(
                    onSignupTap = {
                        navController.navigate(Route.SIGNUP_SCREEN)
                    }
                )
            }
        }
    }
}


