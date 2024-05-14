package com.jimmono.whatamovie.main.presentation.main

import android.content.ContentValues
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.util.ui_shared_components.CTextField
import com.jimmono.whatamovie.util.ui_shared_components.CButton
import com.jimmono.whatamovie.ui.theme.font
import com.jimmono.whatamovie.util.Route

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

@Composable
fun SignupScreen(
    navController: NavController,
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvents) -> Unit
) {
    // State variables for storing user input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Firebase authentication instance
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
                    text = "Sign Up",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = font,
                        fontWeight = FontWeight(500),
                        color = Color.White
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Sign up now for free and start meditating, and explore Medic.",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = font,
                        color = Color(0xB2FFFFFF)
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 24.dp)
                )

                CTextField(
                    hint = "Email Address",
                    value = email,
                    onValueChange = { email = it }
                )

                CTextField(
                    hint = "Password",
                    value = password,
                    onValueChange = { password = it },
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Button
                CButton(
                    text = "Sign Up",
                    onClick = {
                        // Perform registration logic
                        registerUser(auth, email, password)
                        navController.navigate(Route.LOGIN_SCREEN)
                    }

                )

                Row(
                    modifier = Modifier.padding(top = 12.dp, bottom = 52.dp)
                ) {
                    Text(
                        "Already have an account? ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = font,
                            color = Color.White
                        )
                    )

                    Text(
                        "Sign In",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = font,
                            fontWeight = FontWeight(800),
                            color = Color.White
                        ),
                        modifier = Modifier.clickable {
                            navController.navigate(Route.LOGIN_SCREEN)
                        }
                    )
                }
            }
        }
    }
}

fun registerUser(auth: FirebaseAuth, email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.tag(ContentValues.TAG).d("createUserWithEmail:success")
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Timber.tag(ContentValues.TAG).d("Email sent.")

                        }
                    }
            } else {
                // If sign in fails, display a message to the user.
                Timber.tag(ContentValues.TAG).w(task.exception, "createUserWithEmail:failure")


            }
        }
}
