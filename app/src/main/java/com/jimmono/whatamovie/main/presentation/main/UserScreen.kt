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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.jimmono.whatamovie.util.Route
import com.jimmono.whatamovie.util.ui_shared_components.CButton

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun UserScreen(
    navController: NavController,
    bottomBarNavController: NavHostController,
    mainUiState: MainUiState,
    onEvent: (MainUiEvents) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "User Profile")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User profile image (if available)

            Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile picture placeholder",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

            Spacer(modifier = Modifier.height(16.dp))

            // User name
            Text(
                text = "name",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )

            // User email
            Text(text = "email")

            Spacer(modifier = Modifier.height(16.dp))

            // Edit profile button (optional)
            CButton(
                text = "Edit Profile",
                onClick = {
                }

            )

            Spacer(modifier = Modifier.height(16.dp))

            CButton(
                text = "Log Out",
                onClick = {
                }

            )
        }
    }
}