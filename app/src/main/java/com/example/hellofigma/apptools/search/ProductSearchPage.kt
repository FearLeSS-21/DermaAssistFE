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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.facediseases.acne.ProductCard
import com.example.hellofigma.facediseases.acne.ProductGrid
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

// Sample Product data class
data class Product(val id: String, val name: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {
    // Sample product list (replace with ViewModel or repository in a real app)
    val products = remember {
        listOf(
            Product("0", "Cleanser", "Gentle face cleanser"),
            Product("1", "Moisturizer", "Hydrating cream"),
            Product("2", "Serum", "Vitamin C serum"),
            Product("3", "Sunscreen", "SPF 50 protection")
        )
    }
    var searchQuery by remember { mutableStateOf("") }

    // Filter products based on search query
    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Search Products",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4741A6),
                            style = TextStyle(fontFamily = FontFamily.Cursive)
                        )
                        Text(
                            text = "Find Your Skin Care",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBFBFB))
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = Color(0xFFFBFBFB),
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search input
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE6E6E6), RoundedCornerShape(12.dp)),
                placeholder = { Text("Search by name, product, etc.") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(24.dp))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE6E6E6),
                    unfocusedContainerColor = Color(0xFFE6E6E6),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF4741A6)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { /* Triggered on search key press */ }),
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = if (filteredProducts.isEmpty()) "No Results Found" else "Search Results",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f) // Fill available space
                ) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        ProductCard(
                            navController = navController,
                            productId = product.id.toIntOrNull() ?: 0 // Convert String id to Int, default to 0 if invalid
                        )
                    }
                }
            } else {
                Text(
                    text = "Explore Products",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                ProductGrid(
                    navController = navController,
                    modifier = Modifier.weight(1f) // Fill available space
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun SearchPagePreview() {
    MaterialTheme {
        SearchPage(navController = rememberNavController())
    }
}