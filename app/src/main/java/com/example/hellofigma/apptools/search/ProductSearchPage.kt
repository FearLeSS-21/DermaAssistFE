package com.example.hellofigma.apptools.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*
import com.example.hellofigma.ui.utils.Product
import com.example.hellofigma.R
import com.example.hellofigma.ui.components.ProductCard
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {

    val products = remember {
        listOf(
            Product(
                imageRes = R.drawable.ac2,
                name = "Moisturizer",
                description = "Hydrating cream",
                size = "50g",
                usage = "Apply after cleansing",
                ingredients = "Shea Butter, Glycerin",
                price = Random.nextInt(200, 501)
            ),
            Product(
                imageRes = R.drawable.ac3,
                name = "Serum",
                description = "Vitamin C serum",
                size = "30ml",
                usage = "Apply on clean skin",
                ingredients = "Vitamin C, Hyaluronic Acid",
                price = Random.nextInt(200, 501)
            ),
            Product(
                imageRes = R.drawable.ac4,
                name = "Sunscreen",
                description = "SPF 50 protection",
                size = "60ml",
                usage = "Apply before sun exposure",
                ingredients = "Zinc Oxide, Titanium Dioxide",
                price = Random.nextInt(200, 501)
            ),
            Product(
                imageRes = R.drawable.ac5,
                name = "Acne Cream",
                description = "Reduces acne scars",
                size = "50g",
                usage = "Apply twice daily",
                ingredients = "Salicylic Acid, Tea Tree Oil",
                price = Random.nextInt(200, 501)
            ),
            Product(
                imageRes = R.drawable.ac6,
                name = "Eczema Relief",
                description = "Moisturizes skin",
                size = "100g",
                usage = "Apply after bathing",
                ingredients = "Colloidal Oatmeal, Glycerin",
                price = Random.nextInt(200, 501)
            ),
            Product(
                imageRes = R.drawable.ac7,
                name = "Dark Spot Serum",
                description = "Brightens skin",
                size = "30ml",
                usage = "Apply on affected areas",
                ingredients = "Niacinamide, Vitamin C",
                price = Random.nextInt(200, 501)
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello Zeyad!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary
                        )
                        Text(
                            text = "Search Products",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BottomNavBackground)
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = BottomNavBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SearchBarBackground, RoundedCornerShape(12.dp)),
                placeholder = { Text("Search by name, product, etc.") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SearchBarBackground,
                    unfocusedContainerColor = SearchBarBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Primary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { })
            )

            Spacer(Modifier.height(16.dp))

            val displayProducts = if (searchQuery.isNotEmpty()) filteredProducts else products

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = if (filteredProducts.isEmpty()) "No Results Found" else "Search Results",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Text(
                    "Explore Products",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayProducts.size) { index ->
                    ProductCard(product = displayProducts[index])
                }
            }
        }
    }
}
