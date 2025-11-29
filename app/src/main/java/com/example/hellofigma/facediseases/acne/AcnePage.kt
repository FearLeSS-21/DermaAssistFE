package com.example.hellofigma.facediseases.acne

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
fun Acne(navController: NavController, modifier: Modifier = Modifier) {

    val products = listOf(
        Product(R.drawable.ac1, "Acne-Prone Skin Cream", "Reduces Acne Scars", "50g", "Apply twice daily.", "Tea Tree Oil", Random.nextInt(200, 501)),
        Product(R.drawable.ac2, "Facial Cleanser Gel", "Controls Oil Production", "100ml", "Cleanse with cotton pad.", "Tea Tree Oil", Random.nextInt(200, 501)),
        Product(R.drawable.ac3, "Acne-Prone Skin Soap", "Soothes Acne-Prone Skin", "90g", "Use after cleansing.", "Tea Tree Oil", Random.nextInt(200, 501)),
        Product(R.drawable.ac4, "Acne-Prone Skin Hydrate", "Hydrates Acne-Prone Skin", "200g", "Massage post-cleansing.", "Ceramide, Vitamins B & E", Random.nextInt(200, 501)),
        Product(R.drawable.ac5, "Facial Cleanser Gel", "Clears Pores", "100ml", "Apply morning and night.", "Tea Tree Oil", Random.nextInt(200, 501)),
        Product(R.drawable.ac6, "Acne-Prone Skin Lotion", "Fights Acne Bacteria", "230ml", "Apply to affected areas.", "Tea Tree Oil", Random.nextInt(200, 501)),
        Product(R.drawable.ac7, "Facial Cleanser Gel", "Brightens Acne Marks", "250ml", "Use at night on scars.", "Vitamin E and C", Random.nextInt(200, 501)),
        Product(R.drawable.ac8, "Facial Cleanser", "Good Product for Acne", "300ml", "Apply before moisturizer.", "Tea Tree Oil, Chamomile", Random.nextInt(200, 501))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hello Zeyad!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Market", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Acne", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)

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
fun AcnePreview() {
    Acne(rememberNavController())
}
