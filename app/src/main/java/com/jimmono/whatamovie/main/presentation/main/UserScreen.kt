package com.jimmono.whatamovie.main.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.jimmono.whatamovie.util.Route
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.jimmono.whatamovie.ui.theme.font
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserScreen(
    navController: NavController,
    bottomBarNavController: NavHostController,
    mainUiState: MainUiState,
    onEvent: (MainUiEvents) -> Unit,

) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Display the toast message if it's not null
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        toastMessage = null

    }

    Scaffold() {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // User profile image (if available)
            user?.photoUrl?.let {
                Image(
                    painter = rememberImagePainter(data = it),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile picture placeholder",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            // User name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Username: ",
                    style = TextStyle(fontSize = 16.sp, fontFamily = font)
                )
                Text(
                    text = user?.displayName ?: "No Name",
                    style = TextStyle(fontSize = 16.sp, fontFamily = font)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User email
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Email: ",
                    style = TextStyle(fontSize = 16.sp, fontFamily = font)
                )
                Text(
                    text = user?.email ?: "No Email",
                    style = TextStyle(fontSize = 16.sp, fontFamily = font)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Edit profile button
            Button(
                onClick = {
                    navController.navigate(Route.EDIT_PROFILE)
                          },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Edit Profile", style = TextStyle(fontSize = 16.sp))
            }

            // Logout button
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate(Route.LOGIN_SCREEN) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                    toastMessage = "Logged out successfully"
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Log Out", style = TextStyle(fontSize = 16.sp, color = Color.White))
            }
        }
    }
}