package com.example.hellofigma.facediseases.wrinkles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.components.ProductCard
import com.example.hellofigma.ui.theme.*
import com.example.hellofigma.ui.utils.Product
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wrinkles(navController: NavController, modifier: Modifier = Modifier) {

    val products = listOf(
        Product(R.drawable.wr1, "Hollywood Reinvented Bootcamp", "Reduces wrinkles with 2.5% Retinol", "30ml", "Apply at night on face.", "Retinol, Shea Butter Oil", Random.nextInt(200, 501)),
        Product(R.drawable.wr2, "Instantly Ageless", "Instantly firms aging skin", "15ml", "Use after cleansing.", "Argireline, Matrixyl", Random.nextInt(200, 501)),
        Product(R.drawable.wr3, "Neutrogena Retinol Moisturizer", "Hydrates fine lines with Retinol", "29ml", "Massage post-cleansing.", "Retinol, Glucose Complex", Random.nextInt(200, 501)),
        Product(R.drawable.wr4, "Amruth Anti-Aging Facial Cream", "Boosts collagen with natural ingredients", "50g", "Apply morning and night.", "Bio Olive Oil, Argan Oil", Random.nextInt(200, 501)),
        Product(R.drawable.wr5, "Vichy Liftactiv Night Cream", "Lifts sagging skin with H.A.", "50ml", "Use at night on face.", "Hyaluronic Acid, Pro-Xylane", Random.nextInt(200, 501)),
        Product(R.drawable.wr6, "Olay Regenerist", "Regenerates skin cells", "50ml", "Apply daily", "Niacinamide", Random.nextInt(200, 501)),
        Product(R.drawable.wr7, "L'Oréal Revitalift", "Firms and brightens", "50ml", "Use twice daily", "Pro-Retinol", Random.nextInt(200, 501))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hello Mohab!\nWelcome To DermaAssist", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Primary) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Market", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Wrinkles", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(products.size) { index ->
                    ProductCard(product = products[index])
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun WrinklesPreview() {
    Wrinkles(rememberNavController())
}
