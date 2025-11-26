package com.example.hellofigma.appmainpages.minimarket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Minimarket(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello (Zeyad)",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4741A6),
                            style = TextStyle(fontFamily = FontFamily.Cursive)
                        )
                        Text(
                            text = "Welcome To DermaAssist",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/search/SearchPage.kt") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/wishlist/WishlistPage.kt") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Wishlist")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFBFBFB)
                )
            )
        },
        bottomBar = {
            ReusableBottomNavigationBar(navController)
        },
        containerColor = Color(0xFFFBFBFB),
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "Top Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TopCategories(navController)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Best Selling Products",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ProductGrid(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TopCategories(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryItem(
            imageRes = R.drawable.mini_marketx_acne_image,
            label = "Acne",
            onClick = { navController.navigate("com/example/hellofigma/facediseases/acne/acne.kt") }
        )
        CategoryItem(
            imageRes = R.drawable.mini_marketx_eczema,
            label = "Eczema",
            onClick = { navController.navigate("com/example/hellofigma/facediseases/eczema/EczemaPage.kt") }
        )
        CategoryItem(
            imageRes = R.drawable.mini_marketx_eye_black,
            label = "Dark Circles",
            onClick = { navController.navigate("com/example/hellofigma/facediseases/darkspots/DarkEyeSpotsPage.kt") }
        )
        CategoryItem(
            imageRes = R.drawable.mini_marketx_wrnkles,
            label = "Wrinkles",
            onClick = { navController.navigate("com/example/hellofigma/facediseases/wrinkles/WrinklePage.kt") }
        )
    }
}

@Composable
fun CategoryItem(imageRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() }
    ) {
        Card(
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardColors(
                containerColor = Color(0xFFE6E6E6),
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFD3D3D3), shape = CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductGrid(navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        items(4) { index ->
            when (index) {
                0 -> ProductCard(navController, imageRes = R.drawable.ac1, name = "CeraVe Acne Control Gel", description = "Targets acne with 2% Salicylic Acid", price = "300 EGP", categoryRoute = "com/example/hellofigma/facediseases/acne/acne.kt")
                1 -> ProductCard(navController, imageRes = R.drawable.ec1, name = "Eucerin Eczema Relief", description = "Soothes eczema with Ceramide-3", price = "450 EGP", categoryRoute = "com/example/hellofigma/facediseases/eczema/EczemaPage.kt")
                2 -> ProductCard(navController, imageRes = R.drawable.eb1, name = "Cetaphil Hydrating Eye Gel", description = "Hydrates and brightens under-eye", price = "250 EGP", categoryRoute = "com/example/hellofigma/facediseases/darkspots/DarkEyeSpotsPage.kt")
                3 -> ProductCard(navController, imageRes = R.drawable.wr1, name = "Olay Regenerist Cream", description = "Reduces wrinkles with Niacinamide", price = "400 EGP", categoryRoute = "com/example/hellofigma/facediseases/wrinkles/WrinklePage.kt")
            }
        }
    }
}

@Composable
fun ProductCard(navController: NavController, imageRes: Int, name: String, description: String, price: String, categoryRoute: String) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = Color(0xFFE6E6E6),
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        modifier = Modifier
            .width(160.dp) // Fixed width for uniform size
            .height(240.dp) // Fixed height for uniform size
            .clickable { showDialog = true }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF7C7B7B),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Cart",
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF4741A6), shape = RoundedCornerShape(8.dp))
                    .padding(4.dp),
                tint = Color.White
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Product Details", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Image(
                        painter = painterResource(imageRes),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(Color(0xFFD3D3D3), shape = RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = description, fontSize = 14.sp, color = Color(0xFF7C7B7B))
                    Text(text = price, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false; navController.navigate(categoryRoute) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Category", color = Color(0xFF4741A6))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color(0xFF4741A6))
                }
            },
            modifier = Modifier
                .width(300.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 865)
@Composable
fun MinimarketPreview() {
    MaterialTheme {
        Minimarket(navController = rememberNavController())
    }
}