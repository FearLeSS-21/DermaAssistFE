package com.example.hellofigma.appmainpages.openingpage

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.example.hellofigma.ui.theme.*

import kotlinx.coroutines.delay

@Composable
fun AppOpeningPage(navController: NavController) {
    val words = "DermaAssist".map { it.toString() }
    var animatedWords by remember { mutableStateOf(listOf<String>()) }
    var showLine by remember { mutableStateOf(false) }
    var showStar by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundGradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GradientOffset"
    )

    LaunchedEffect(Unit) {
        animatedWords = listOf(words[0])
        delay(400L)
        words.drop(1).forEachIndexed { index, word ->
            delay(70L)
            animatedWords = words.take(index + 2)
        }
        delay(150L)
        showLine = true
        delay(300L)
        showStar = true
        delay(1800L)
        navController.navigate("com/example/hellofigma/appmainpages/register/LoginPage.kt") {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .wrapContentWidth()
            ) {
                animatedWords.forEachIndexed { index, word ->
                    AnimatedWord(word = word, isFirstLetter = index == 0)
                }
            }
            if (showLine) {
                AnimatedLine()
            }
        }
    }
}

@Composable
fun AnimatedWord(word: String, isFirstLetter: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (word.isNotEmpty()) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = if (isFirstLetter) Spring.StiffnessLow else Spring.StiffnessMedium
        ),
        label = "WordAlpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (isFirstLetter && alpha > 0f) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "WordScale"
    )

    Text(
        text = word,
        style = TextStyle(
            fontSize = if (isFirstLetter) 52.sp else 48.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
            brush = Brush.linearGradient(
                colors = listOf(
                    Primary,
                    Primary
                )
            ),
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .alpha(alpha)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .padding(horizontal = 1.dp)
    )
}

@Composable
fun AnimatedLine() {
    val lineWidth by animateDpAsState(
        targetValue = 220.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "LineWidth"
    )

    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .width(lineWidth)
            .height(4.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Primary,
                        LinkBlue
                    )
                )
            )
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewOpeningPage() {
    AppOpeningPage(navController = rememberNavController())
}
