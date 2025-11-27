package com.example.hellofigma.appmainpages.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsPlan(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = LightGray,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserHeader()
                ProfileStatsTabs(navController)
                ReportTabs(navController)
                ConditionProgressBars()
            }
        }
    }
}

@Composable
private fun UserHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = "Zeyad Wael",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = GrayText
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .padding(top = 8.dp),
            tint = Primary
        )
    }
}

@Composable
fun ProfileStatsTabs(navController: NavController) {
    TabRow(
        selectedTabIndex = if (navController.currentDestination?.route == "com/example/hellofigma/appmainpages/profile/statsPage.kt") 1 else 0,
        modifier = Modifier.padding(vertical = 8.dp),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[if (navController.currentDestination?.route == "com/example/hellofigma/appmainpages/profile/statsPage.kt") 1 else 0]),
                color = Primary
            )
        }
    ) {
        listOf(
            Pair("Profile", "com/example/hellofigma/appmainpages/profile/PersonalDataPage.kt"),
            Pair("Stats", "com/example/hellofigma/appmainpages/profile/statsPage.kt")
        ).forEach { (title, route) ->
            Tab(
                selected = navController.currentDestination?.route == route,
                onClick = {
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                },
                text = {
                    Text(
                        title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (navController.currentDestination?.route == route) Primary else GrayText
                    )
                }
            )
        }
    }
}

@Composable
private fun ReportTabs(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        Triple("Today’s Report", "com/example/hellofigma/appmainpages/profile/results.kt", Icons.Default.Leaderboard),
        Triple("Analysis", "com/example/hellofigma/appmainpages/profile/ProgTrack.kt", Icons.Default.Analytics)
    )

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.padding(vertical = 8.dp),
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = Primary
            )
        }
    ) {
        tabs.forEachIndexed { index, (title, route, _) ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                text = {
                    Text(
                        title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTabIndex == index) Primary else GrayText
                    )
                }
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val route = tabs[selectedTabIndex].second
                route?.let {
                    navController.navigate(it) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        restoreState = true
                    }
                }
            },
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = GrayText
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            val (title, route, icon) = tabs[selectedTabIndex]
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                tint = Primary
            )
            Text(
                text = if (selectedTabIndex == 0) "Today’s Report Content" else "Analysis Content",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ConditionProgressBars() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = White,
            contentColor = GrayText
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            listOf(
                Triple("Acne", 0.7f, "70%\nHigh"),
                Triple("Eczema", 0.6f, "60%\nMid"),
                Triple("Dark Circles", 0.4f, "40%\nLow"),
                Triple("Wrinkles", 0.4f, "40%\nLow")
            ).forEachIndexed { index, (title, progress, percentage) ->
                val icon = when (title) {
                    "Acne" -> Icons.Default.BugReport
                    "Eczema" -> Icons.Default.Healing
                    "Dark Circles" -> Icons.Default.Visibility
                    "Wrinkles" -> Icons.Default.FaceRetouchingNatural
                    else -> Icons.Default.Info
                }

                ConditionItem(
                    icon = icon,
                    title = title,
                    progress = progress,
                    percentage = percentage,
                    modifier = Modifier.padding(bottom = if (index < 3) 12.dp else 0.dp)
                )
            }
        }
    }
}

@Composable
private fun ConditionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    progress: Float,
    percentage: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .alpha(0.2f),
            tint = Primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GrayText
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(124.dp))
                    .background(LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(124.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary, LinkBlue)
                            )
                        )
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = percentage,
            fontSize = 11.sp,
            color = GrayText,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

@Preview(widthDp = 412, heightDp = 865)
@Composable
private fun StatsPlanPreview() {
    HelloFigmaTheme {
        StatsPlan(navController = rememberNavController())
    }
}
