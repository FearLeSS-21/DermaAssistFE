package com.example.hellofigma.appmainpages.openingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.ui.animations.*
import com.example.hellofigma.ui.theme.*

@Composable
fun AppOpeningPage(navController: NavController) {
    val words = "DermaAssist".map { it.toString() }

    // Gradient animation
    val gradientOffset by animateOpeningGradient()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        White.copy(alpha = 0.95f),
                        White.copy(alpha = 0.8f),
                        LightGray.copy(alpha = 0.6f)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, gradientOffset * 1000f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, (1f - gradientOffset) * 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            words.forEachIndexed { index, word ->
                AnimatedWord(word = word, isFirstLetter = index == 0)
            }
        }
    }

    // Navigate after a fixed time (matching the animation duration)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2500L)
        navController.navigate("com/example/hellofigma/appmainpages/register/LoginPage.kt") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }
}

@Composable
fun AnimatedWord(word: String, isFirstLetter: Boolean) {
    val alpha = animatedWordAlpha(true)  // Fade-in
    val scale = animatedWordScale(isFirstLetter, alpha) // Pop for first letter

    Text(
        text = word,
        style = TextStyle(
            fontSize = if (isFirstLetter) 52.sp else 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            brush = Brush.linearGradient(listOf(Primary, Primary)),
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .alpha(alpha)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .padding(horizontal = 1.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOpeningPage() {
    AppOpeningPage(navController = rememberNavController())
}
