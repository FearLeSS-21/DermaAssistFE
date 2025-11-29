package com.example.hellofigma.facediseases.wrinkles

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wrinkles(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Hello Mohab!\nWelcome To DermaAssist", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary) }, actions = { IconButton(onClick = { navController.navigate("search") }) { Icon(Icons.Default.Search, null) }; IconButton(onClick = { navController.navigate("minimarket") }) { Icon(Icons.Default.ShoppingCart, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BottomNavBackground)) },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = BottomNavBackground,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            Text("Market", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Wrinkles", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            ProductGrid(navController, Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductGrid(navController: NavController, modifier: Modifier = Modifier) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier.fillMaxWidth()) {
        items(7) { ProductCard(navController, it) }
    }
}

@Composable
fun ProductCard(navController: NavController, productId: Int) {
    var showDialog by remember { mutableStateOf(false) }
    val images = listOf(R.drawable.wr1, R.drawable.wr2, R.drawable.wr3, R.drawable.wr4, R.drawable.wr5, R.drawable.wr6, R.drawable.wr7)
    val productNames = listOf("Hollywood Reinvented Bootcamp", "Instantly Ageless", "Neutrogena Retinol Moisturizer", "Amruth Anti-Aging Facial Cream", "Vichy Liftactiv Night Cream", "Olay Regenerist", "L'Oréal Revitalift")
    val descriptions = listOf("Reduces wrinkles with 2.5% Retinol", "Instantly firms aging skin", "Hydrates fine lines with Retinol", "Boosts collagen with natural ingredients", "Lifts sagging skin with H.A.", "Regenerates skin cells", "Firms and brightens")
    val sizes = listOf("30ml", "15ml", "29ml", "50g", "50ml", "50ml", "50ml")
    val usageInstructions = listOf("Apply at night on face.", "Use after cleansing.", "Massage post-cleansing.", "Apply morning and night.", "Use at night on face.", "Apply daily", "Use twice daily")
    val keyIngredients = listOf("Retinol, Shea Butter Oil", "Argireline, Matrixyl", "Retinol, Glucose Complex", "Bio Olive Oil, Argan Oil", "Hyaluronic Acid, Pro-Xylane", "Niacinamide", "Pro-Retinol")
    val imageRes = images[productId % images.size]
    val productName = productNames[productId % productNames.size]
    val description = descriptions[productId % descriptions.size]
    val size = sizes[productId % sizes.size]
    val usageInstruction = usageInstructions[productId % usageInstructions.size]
    val keyIngredient = keyIngredients[productId % keyIngredients.size]
    val randomPrice = Random.nextInt(200, 501)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SearchBarBackground),
        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f).clickable { showDialog = true }
    ) {
        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Image(painter = painterResource(imageRes), contentDescription = "Product Image", contentScale = ContentScale.Fit, modifier = Modifier.size(140.dp).background(SearchBarBackground).clip(RoundedCornerShape(8.dp)))
            Spacer(Modifier.height(8.dp))
            Text(productName, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column(Modifier.padding(12.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Image(painter = painterResource(imageRes), contentDescription = "Product Image", contentScale = ContentScale.Fit, modifier = Modifier.size(100.dp).background(SearchBarBackground).clip(RoundedCornerShape(8.dp)))
                        Text(productName, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    }
                    Text(description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary, textAlign = TextAlign.Justify, lineHeight = 18.sp)
                    Text("Size: $size", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BlackText)
                    Text("Use: $usageInstruction", fontSize = 12.sp, color = TextSecondary)
                    Text("Ingredients: $keyIngredient", fontSize = 12.sp, color = TextSecondary)
                    Text("$randomPrice EGP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.align(Alignment.End))
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Close", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                }
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(White)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun WrinklesPreview() {
    MaterialTheme { Wrinkles(rememberNavController()) }
}