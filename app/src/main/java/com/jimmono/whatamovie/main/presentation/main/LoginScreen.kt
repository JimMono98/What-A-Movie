package com.jimmono.whatamovie.main.presentation.main

import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
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
                    text = "Sign In",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = font,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Text(
                    "Sign In now to access to our platform now!",
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

                // Text Fields
                CTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email Address",
                )

                CTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                    visualTransformation = PasswordVisualTransformation(),
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
                                    val user = auth.currentUser
                                    Timber.tag(ContentValues.TAG).d("signInWithEmail:success")
                                    toastMessage = "Sign-in successful"
                                    navController.navigate(Route.MEDIA_MAIN_SCREEN)
                                } else {
                                    Timber.tag(ContentValues.TAG).w(task.exception, "signInWithCredential:failure")
                                    toastMessage = "Sign-in failed: ${task.exception?.message}"
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