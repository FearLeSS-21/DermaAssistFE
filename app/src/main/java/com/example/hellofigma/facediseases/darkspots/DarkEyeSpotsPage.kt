package com.example.hellofigma.facediseases.darkspots

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import kotlin.random.Random

private val PrimaryColor = Color(0xFF4741A6)
private val BackgroundColor = Color(0xFFFBFBFB)
private val CardBackgroundColor = Color(0xFFE6E6E6)
private val SecondaryTextColor = Color(0xFF7C7B7B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dark(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello Mohab!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryColor,
                            style = TextStyle(fontFamily = FontFamily.Cursive),
                            modifier = Modifier.semantics { contentDescription = "Greeting" }
                        )
                        Text(
                            text = "Welcome To DermaAssist",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { contentDescription = "Welcome message" }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navController.navigate("minimarket") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = BackgroundColor,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Market",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp).semantics { contentDescription = "Market header" }
            )
            Text(
                text = "Dark Spots",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp).semantics { contentDescription = "Dark Spots header" }
            )
            ProductGrid(navController, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
            SeeMoreButton(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductGrid(navController: NavController, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(8) { index ->
            ProductCard(navController, productId = index)
        }
    }
}

@Composable
fun ProductCard(navController: NavController, productId: Int) {
    var showDialog by remember { mutableStateOf(false) }
    val images = listOf(
        R.drawable.eb1, R.drawable.eb2, R.drawable.eb4, R.drawable.eb5,
        R.drawable.eb6, R.drawable.eb7, R.drawable.eb8
    )
    val productNames = listOf("Vichy Déalia Eye Cream", "Avene Retinal Eye Cream", "Kiehl's Clearly Corrective", "La Roche-Posay Pigmentclar", "Cleo Glow Hyaluronic Concealer", "Cetaphil Hydrating Eye Gel-Cream", "The Ordinary Caffeine Solution")
    val descriptions = listOf(
        "Reduces puffiness and dark circles", "Evens skin tone with Retinal", "Brightens under-eye area with SPF 30", "Corrects hyperpigmentation", "Lightens dark marks with Hyaluronic Acid", "Hydrates and brightens delicate skin", "Reduces puffiness with Caffeine"
    )
    val sizes = listOf("15ml", "15ml", "15ml", "20ml", "15ml", "14ml", "30ml")
    val usageInstructions = listOf(
        "Apply around eyes at night.", "Use at night on eye area.", "Apply morning under eyes.", "Use morning and night on spots.", "Apply after cleansing.", "Use twice daily around eyes.", "Apply morning and night."
    )
    val keyIngredients = listOf("Hyaluronic Acid, Caffeine", "Retinal, Hyaluronic Acid", "Vitamin C, Eclipta Prostrata", "Niacinamide, Thermal Water", "Hyaluronic Acid", "Hyaluronic Acid", "Caffeine, EGCG")
    val imageRes = images[productId % images.size]
    val productName = productNames[productId % productNames.size]
    val description = descriptions[productId % descriptions.size]
    val size = sizes[productId % sizes.size]
    val usageInstruction = usageInstructions[productId % usageInstructions.size]
    val keyIngredient = keyIngredients[productId % keyIngredients.size]
    val randomPrice = Random.nextInt(200, 501)

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { showDialog = true }
            .semantics { contentDescription = "Product card for $productName" }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Product Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = productName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { contentDescription = "Product name" }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showDialog) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = "Product Image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(
                                text = productName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.SansSerif,
                                color = PrimaryColor,
                                modifier = Modifier.weight(1f).padding(start = 12.dp)
                                    .semantics { contentDescription = "Product name in dialog" }
                            )
                        }
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF555555),
                            textAlign = TextAlign.Justify,
                            lineHeight = 18.sp,
                            modifier = Modifier.semantics { contentDescription = "Product description in dialog" }
                        )
                        Text(
                            text = "Size: $size",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.Black,
                            modifier = Modifier.semantics { contentDescription = "Product size" }
                        )
                        Text(
                            text = "Use: $usageInstruction",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF555555),
                            lineHeight = 16.sp,
                            modifier = Modifier.semantics { contentDescription = "Usage instructions" }
                        )
                        Text(
                            text = "Ingredients: $keyIngredient",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFF555555),
                            lineHeight = 16.sp,
                            modifier = Modifier.semantics { contentDescription = "Key ingredients" }
                        )
                        Text(
                            text = "$randomPrice EGP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            color = PrimaryColor,
                            modifier = Modifier.align(Alignment.End).semantics { contentDescription = "Product price in dialog" }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Close",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = PrimaryColor
                        )
                    }
                },
                modifier = Modifier.widthIn(max = 320.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)
            )
        }
    }
}

@Composable
fun SeeMoreButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("minimarket") },
        modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryColor),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor, contentColor = Color.White),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(
            text = "See More",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun DarkPreview() {
    MaterialTheme {
        Dark(navController = rememberNavController())
    }
}