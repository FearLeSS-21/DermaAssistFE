package com.example.hellofigma.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, modifier: Modifier = Modifier) {
    var selectedNavItem by remember { mutableStateOf("Settings") }
    var isVisible by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DermaAssist",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/search/SearchPage.kt") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                    }
                    IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/wishlist/WishlistPage.kt") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Primary
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                ReusableBottomNavigationBar(navController = navController)
            }
        },
        containerColor = White,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(5) { index ->
                val (title, desc, route) = when (index) {
                    0 -> Triple("Account", "Manage your profile", "com/example/hellofigma/appmainpages/profile/PersonalDataPage.kt")
                    1 -> Triple("Notifications", "Notification preferences", "notifications")
                    2 -> Triple("Theme", "Light or dark mode", "theme")
                    3 -> Triple("Language", "Select language", "language")
                    else -> Triple("Logout", "Sign out", "com/example/hellofigma/appmainpages/register/LoginPage.kt")
                }
                SettingsItem(
                    title = title,
                    description = desc,
                    onClick = { navController.navigate(route) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SettingsItem(title: String, description: String, onClick: () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .shadow(
                elevation = if (isHovered) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(LightGray),
        colors = CardDefaults.cardColors(
            containerColor = LightGray,
            contentColor = BlackText
        ),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = BlackText
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate to $title",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .padding(4.dp),
                tint = White
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun SettingsPreview() {
    MaterialTheme {
        Settings(navController = rememberNavController())
    }
}