package com.example.hellofigma.appmainpages.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.R
import com.example.hellofigma.ui.theme.HelloFigmaTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import kotlin.random.Random

enum class Disease { Eczema, Acne, Wrinkles, Eyebags }
enum class Region { Forehead, Nose, LeftCheek, RightCheek, Chin, LeftEye, RightEye }

@Composable
fun Results(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val mainRegions = listOf(Region.Forehead, Region.Nose, Region.LeftCheek, Region.RightCheek, Region.Chin)
    val eyeRegions = listOf(Region.LeftEye, Region.RightEye)
    val diseases = Disease.values().toList()
    val regionDiseases = (mainRegions + eyeRegions).associateWith {
        if (it == Region.LeftEye || it == Region.RightEye) listOf(Disease.Eyebags)
        else diseases.shuffled(Random).take(Random.nextInt(1, 3))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Skin Analysis Results",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4741A6)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FaceDiagnosisDisplay(regionDiseases)

        Spacer(modifier = Modifier.height(12.dp))

        DiagnosisSummary(regionDiseases)

        Spacer(modifier = Modifier.weight(1f))

        BackToHomeButton(navController)
    }
}

@Composable
fun FaceDiagnosisDisplay(regionDiseases: Map<Region, List<Disease>>) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.face_base),
            contentDescription = "Face Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun DiagnosisSummary(regionDiseases: Map<Region, List<Disease>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE0E7FF))
            .padding(12.dp)
    ) {
        Disease.values().forEach { disease ->
            val affectedRegions = regionDiseases.filterValues { it.contains(disease) }.keys
            if (affectedRegions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                        contentDescription = "${disease.name} image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${disease.name}: ${affectedRegions.joinToString { it.name }}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4741A6),
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BackToHomeButton(navController: NavController) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF4741A6))
            .clickable {
                navController.navigate("com/example/hellofigma/appmainpages/homepage/HomePage.kt")
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Back to Home",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Preview
@Composable
fun ResultsPreview() {
    HelloFigmaTheme {
        Results(navController = rememberNavController())
    }
}
