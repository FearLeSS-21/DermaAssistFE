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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.facediseases.acne.ProductCard
import com.example.hellofigma.facediseases.acne.ProductGrid
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*  // ← Brings in Primary, BottomNavBackground, SearchBarBackground

data class Product(val id: String, val name: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(navController: NavController, modifier: Modifier = Modifier) {
    val products = remember {
        listOf(
            Product("0", "Cleanser", "Gentle face cleanser"),
            Product("1", "Moisturizer", "Hydrating cream"),
            Product("2", "Serum", "Vitamin C serum"),
            Product("3", "Sunscreen", "SPF 50 protection")
        )
    }
    var searchQuery by remember { mutableStateOf("") }
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
                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                            color = Primary,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
                        )
                        Text(
                            text = "Find Your Skin Care",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
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

            if (searchQuery.isNotEmpty()) {
                Text(
                    text = if (filteredProducts.isEmpty()) "No Results Found" else "Search Results",
                    fontSize = 22.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredProducts.size) { index ->
                        ProductCard(
                            navController = navController,
                            productId = filteredProducts[index].id.toIntOrNull() ?: 0
                        )
                    }
                }
            } else {
                Text("Explore Products", fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                ProductGrid(navController = navController, modifier = Modifier.weight(1f))
            }
        }
    }
}