package com.jimmono.whatamovie.main.presentation.main

import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Display the toast message if it's not null
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        toastMessage = null
    }

    Surface(
        color= MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // Logo
                Spacer(modifier = Modifier.height(220.dp))


                Text(
                    text = "Register",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = font,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Register now for free and start rating movies.",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = font,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                    visualTransformation = PasswordVisualTransformation()

                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Button
                CButton(
                    text = "Sign Up",
                    onClick = {
                        // Perform registration logic
                        registerUser(auth, email, password) { message ->
                            toastMessage = message
                            if (message.contains("success")) {
                                navController.navigate(Route.LOGIN_SCREEN)
                            }
                        }
                    }
                )
                Row(
                    modifier = Modifier.padding(top=12.dp, bottom = 52.dp)
                ){
                    Text("Already have an account? ",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = font,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(5.dp)
                    )
                    androidx.compose.material.Surface(
                        shape = MaterialTheme.shapes.large,
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                    ) {
                        Text("Sign In",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font,
                                fontWeight = FontWeight(800),
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate(Route.LOGIN_SCREEN)
                            }
                                .padding(5.dp)
                        )
                    }

                }
            }
        }
    }
}

fun registerUser(auth: FirebaseAuth, email: String, password: String, callback: (String) -> Unit) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Timber.tag(ContentValues.TAG).d("createUserWithEmail:success")
                callback("Registration successful! Verification email sent.")
                FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful) {
                            Timber.tag(ContentValues.TAG).d("Email sent.")
                        }
                    }
            } else {
                // If sign in fails, display a message to the user.
                Timber.tag(ContentValues.TAG).w(task.exception, "createUserWithEmail:failure")
                callback("Registration failed: ${task.exception?.message}")
            }
        }
}

