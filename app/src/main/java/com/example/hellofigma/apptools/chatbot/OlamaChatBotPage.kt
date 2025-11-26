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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoryVisionWebViewPage(navController: NavController) {
    val context = LocalContext.current

    // Launch Telegram Bot URL using Intent
    LaunchedEffect(Unit) {
        val telegramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/DoryVision_bot"))
        context.startActivity(telegramIntent)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("DoryVision Bot") })
        },
        bottomBar = { ReusableBottomNavigationBar(navController) }
    ) {
        // Optionally, show a message while redirecting
        Text(
            text = "Redirecting to Telegram...",
            modifier = androidx.compose.ui.Modifier.padding(it)
        )
    }
}
