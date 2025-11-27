package com.example.hellofigma.appmainpages.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.R
import com.example.hellofigma.ui.theme.*
import kotlin.random.Random

enum class Disease { Eczema, Acne, Wrinkles, Eyebags }
enum class Region { Forehead, Nose, LeftCheek, RightCheek, Chin, LeftEye, RightEye }

@Composable
fun Results(navController: NavController, modifier: Modifier = Modifier) {
    val mainRegions = listOf(Region.Forehead, Region.Nose, Region.LeftCheek, Region.RightCheek, Region.Chin)
    val eyeRegions = listOf(Region.LeftEye, Region.RightEye)
    val regionDiseases = (mainRegions + eyeRegions).associateWith {
        if (it == Region.LeftEye || it == Region.RightEye) listOf(Disease.Eyebags)
        else Disease.values().toList().shuffled(Random).take(Random.nextInt(1, 3))
    }

    HelloFigmaTheme {
        Box(modifier = modifier.fillMaxSize().background(LightGray)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        "Skin Analysis Results",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item { FaceDisplay(regionDiseases) }

                items(Disease.values().toList()) { disease ->
                    val regions = regionDiseases.filterValues { it.contains(disease) }.keys
                    if (regions.isNotEmpty()) {
                        DiseaseCard(disease, regions, regions.size % 2 != 0)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ActionButton(navController)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun FaceDisplay(regionDiseases: Map<Region, List<Disease>>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(White, LightGray)))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.face_base),
                contentDescription = "Face Analysis",
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun DiseaseCard(disease: Disease, regions: Set<Region>, alignRight: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = when (disease) {
                                Disease.Acne -> listOf(Primary, LinkBlue)
                                Disease.Wrinkles -> listOf(LinkBlue, Primary)
                                Disease.Eczema -> listOf(Primary.copy(alpha = 0.8f), LinkBlue.copy(alpha = 0.8f))
                                Disease.Eyebags -> listOf(GrayText, Primary)
                            }
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(White.copy(alpha = 0.25f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(
                                id = when (disease) {
                                    Disease.Eczema -> R.drawable.mini_marketx_eczema
                                    Disease.Acne -> R.drawable.mini_marketx_acne_image
                                    Disease.Wrinkles -> R.drawable.mini_marketx_wrnkles
                                    Disease.Eyebags -> R.drawable.mini_marketx_eye_black
                                }
                            ),
                            contentDescription = disease.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            disease.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Detected in ${regions.size} ${if (regions.size == 1) "area" else "areas"}",
                            fontSize = 13.sp,
                            color = White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegionChips(regions)
                    }
                }
            }
        }
    }
}

@Composable
fun RegionChips(regions: Set<Region>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        regions.take(3).forEach { region ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = White.copy(alpha = 0.3f),
                modifier = Modifier.height(26.dp)
            ) {
                Text(
                    formatRegionName(region),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (regions.size > 3) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = White.copy(alpha = 0.3f),
                modifier = Modifier.height(26.dp)
            ) {
                Text(
                    "+${regions.size - 3}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("com/example/hellofigma/appmainpages/homepage/HomePage.kt") },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary
        )
    ) {
        Text(
            "Back to Home",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = White
        )
    }
}

fun formatRegionName(region: Region): String = when (region) {
    Region.LeftCheek -> "Left Cheek"
    Region.RightCheek -> "Right Cheek"
    Region.LeftEye -> "Left Eye"
    Region.RightEye -> "Right Eye"
    else -> region.name
}