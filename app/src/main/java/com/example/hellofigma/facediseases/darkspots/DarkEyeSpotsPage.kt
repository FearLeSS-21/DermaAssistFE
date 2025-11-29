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
fun Dark(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hello Mohab!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                        Text("Welcome To DermaAssist", fontSize = 16.sp, color = TextSecondary)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) { Icon(Icons.Default.Search, null) }
                    IconButton(onClick = { navController.navigate("minimarket") }) { Icon(Icons.Default.ShoppingCart, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BottomNavBackground)
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = BottomNavBackground,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(16.dp))
            Text("Market", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Dark Spots", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            ProductGrid(navController, Modifier.weight(1f))
            Spacer(Modifier.height(16.dp))
            SeeMoreButton(navController)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductGrid(navController: NavController, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(7) { ProductCard(navController, it) }
    }
}

@Composable
fun ProductCard(navController: NavController, productId: Int) {
    var showDialog by remember { mutableStateOf(false) }
    val images = listOf(R.drawable.eb1, R.drawable.eb2, R.drawable.eb4, R.drawable.eb6, R.drawable.eb7, R.drawable.eb8, R.drawable.eb8)
    val productNames = listOf("Glamy Lab Hydra Intense Cream", "Eucerin Eczema Relief Cream", "Aveeno Eczema Moisturizing Cream", "Elidel Cream", "Tacrolimus Ointment", "SHAAN Cream", "StarVille Micellar Water")
    val descriptions = listOf("Hydrates all skin types", "Reduces eczema inflammation", "Moisturizes eczema-prone skin", "Treats atopic dermatitis", "For dermatologic use only", "Moisturizes sensitive skin", "Daily hydration for sensitive skin")
    val sizes = listOf("50g", "226g", "73g", "30g", "30g", "453g", "89ml")
    val usageInstructions = listOf("Apply twice daily", "Use on irritated areas", "Apply after cleansing", "Use as directed by doctor", "Apply to affected areas", "Massage post-bath", "Use morning and night")
    val keyIngredients = listOf("Aloe Vera, Chamomile", "Colloidal Oatmeal", "Oat Extract", "Pimecrolimus", "Tacrolimus", "Glycerin, Petrolatum", "Hyaluronic Acid, Ceramides")
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

@Composable
fun SeeMoreButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("minimarket") },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("See More", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = White)
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun DarkPreview() {
    MaterialTheme { Dark(rememberNavController()) }
}