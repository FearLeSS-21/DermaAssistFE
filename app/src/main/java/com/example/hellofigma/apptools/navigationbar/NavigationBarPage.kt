package com.example.hellofigma.apptools.navigationbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.MaterialTheme

@Composable
fun ReusableBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFFFBFBFB),
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "com/example/hellofigma/appmainpages/homepage/HomePage.kt",
            onClick = {
                navController.navigate("com/example/hellofigma/appmainpages/homepage/HomePage.kt") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("ChatBot") },
            selected = currentRoute == "com/example/hellofigma/apptools/chatbot/OlamaChatBotPage.kt",
            onClick = {
                navController.navigate("com/example/hellofigma/apptools/chatbot/OlamaChatBotPage.kt") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF4741A6), shape = RoundedCornerShape(20.dp))
                        .padding(8.dp),
                    tint = Color.White
                )
            },
            label = { Text("Add") },
            selected = currentRoute == "com/example/hellofigma/apptools/cameradetection/CameraDetectionPage.kt",
            onClick = { navController.navigate("com/example/hellofigma/apptools/cameradetection/CameraDetectionPage.kt") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentRoute == "com/example/hellofigma/apptools/settings/AppSettingsPage.kt",
            onClick = {
                navController.navigate("com/example/hellofigma/apptools/settings/AppSettingsPage.kt") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "com/example/hellofigma/appmainpages/profile/PersonalDataPage.kt",
            onClick = {
                navController.navigate("com/example/hellofigma/appmainpages/profile/PersonalDataPage.kt") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}