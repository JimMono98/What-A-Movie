package com.jimmono.whatamovie.main.presentation.main

import android.annotation.SuppressLint
import android.content.ContentValues
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.jimmono.whatamovie.R
import com.jimmono.whatamovie.util.ui_shared_components.CTextField
import com.jimmono.whatamovie.util.ui_shared_components.CButton
import com.jimmono.whatamovie.util.ui_shared_components.DontHaveAccountRow
import com.jimmono.whatamovie.ui.theme.font
import com.jimmono.whatamovie.util.Route
import timber.log.Timber
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.auth.UserProfileChangeRequest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditProfile(
    navController: NavController,
    mainUiState: MainUiState,
    modifier: Modifier = Modifier,
    onEvent: (MainUiEvents) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current
    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Display the toast message if it's not null
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        toastMessage = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Profile", color = Color.White)
                },
                backgroundColor = Color(0xFF253334)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = "Username: ",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)
            )

            Spacer(modifier = Modifier.height(8.dp))

            BasicTextField(
                value = displayName,
                onValueChange = { displayName = it },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Cyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    // Handle onDone action
                })
            )

            Spacer(modifier = Modifier.height(20.dp))

            // User email
            Text(
                text = "Email: ",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user?.email ?: "No Email",
                style = TextStyle(fontSize = 16.sp, color = Color.Cyan)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toastMessage = "Profile updated successfully"
                                navController.navigate(Route.MEDIA_MAIN_SCREEN)
                            } else {
                                toastMessage = "Profile update failed"
                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Update Profile", style = TextStyle(fontSize = 14.sp))
            }
        }
    }
}
