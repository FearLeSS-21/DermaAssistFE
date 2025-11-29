package com.example.hellofigma.apptools.chatbot

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hellofigma.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoryVisionWebViewPage(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val telegramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/DoryVision_bot"))
        context.startActivity(telegramIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DoryVision Bot", color = Primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BottomNavBackground)
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = BottomNavBackground
    ) { paddingValues ->
        Text(
            text = "Redirecting to Telegram...",
            color = BlackText,
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}