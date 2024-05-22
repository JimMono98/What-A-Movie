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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
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
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            if (maxWidth < maxHeight) {
                // Portrait layout
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(80.dp))
                    // User profile image (if available)
                    user?.photoUrl?.let {
                        Image(
                            painter = rememberImagePainter(data = it),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile picture placeholder",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))

                    // User name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Username: ",
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = user?.displayName ?: "No Name",
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 18.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    // User email
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Email: ",
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = user?.email ?: "No Email",
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 18.sp)
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
                        Text(text = "Edit Profile", style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = font,
                            fontSize = 16.sp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

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
                        Text(
                            text = "Log Out",
                            style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 16.sp)
                        )
                    }
                }
            } else {
                // Landscape layout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User profile image (if available)
                        user?.photoUrl?.let {
                            Image(
                                painter = rememberImagePainter(data = it),
                                contentDescription = "Profile picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile picture placeholder",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(50.dp))

                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // User name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Username: ",
                                style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = font,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = user?.displayName ?: "No Name",
                                style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = font,
                                    fontSize = 18.sp)
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
                                style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = font,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = user?.email ?: "No Email",
                                style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = font,
                                    fontSize = 18.sp)
                            )
                        }

                        /*Spacer(modifier = Modifier.height(50.dp))

                        Button(
                            onClick = {
                                navController.navigate(Route.EDIT_PROFILE)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Cyan),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .height(60.dp)
                                .width(200.dp)
                                .padding(vertical = 8.dp)
                        ) {
                            Text(text = "History", style = TextStyle(fontSize = 16.sp))
                        }
                        */
                        Spacer(modifier = Modifier.height(20.dp))

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
                            Text(text = "Edit Profile", style = TextStyle(color = MaterialTheme.colorScheme.onSurface,
                                fontFamily = font,
                                fontSize = 16.sp))
                        }

                        Spacer(modifier = Modifier.height(20.dp))

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
                            Text(
                                text = "Log Out",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = font,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}