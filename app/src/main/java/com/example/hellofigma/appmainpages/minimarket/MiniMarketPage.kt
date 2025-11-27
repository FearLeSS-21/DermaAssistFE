package com.example.hellofigma.appmainpages.minimarket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.*

@Composable
fun Minimarket(navController: NavController, modifier: Modifier = Modifier) {
    HelloFigmaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Hello (Zeyad)",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colors.primary,
                                fontFamily = FontFamily.Cursive
                            )
                            Text(
                                text = "Welcome To DermaAssist",
                                fontSize = 16.sp,
                                color = MaterialTheme.colors.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/search/SearchPage.kt") }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colors.primary)
                        }
                        IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/wishlist/WishlistPage.kt") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Wishlist", tint = MaterialTheme.colors.primary)
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background,
                    elevation = 0.dp
                )
            },
            bottomBar = { ReusableBottomNavigationBar(navController) },
            backgroundColor = MaterialTheme.colors.background,
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
                SectionTitle("Market")
                SectionTitle("Top Categories")
                TopCategories(navController)
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Best Selling Products")
                ProductGrid(navController)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun TopCategories(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryItem(R.drawable.mini_marketx_acne_image, "Acne") {
            navController.navigate("com/example/hellofigma/facediseases/acne/acne.kt")
        }
        CategoryItem(R.drawable.mini_marketx_eczema, "Eczema") {
            navController.navigate("com/example/hellofigma/facediseases/eczema/EczemaPage.kt")
        }
        CategoryItem(R.drawable.mini_marketx_eye_black, "Dark Circles") {
            navController.navigate("com/example/hellofigma/facediseases/darkspots/DarkEyeSpotsPage.kt")
        }
        CategoryItem(R.drawable.mini_marketx_wrnkles, "Wrinkles") {
            navController.navigate("com/example/hellofigma/facediseases/wrinkles/WrinklePage.kt")
        }
    }
}

@Composable
fun CategoryItem(imageRes: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp).clickable { onClick() }
    ) {
        Card(
            shape = CircleShape,
            elevation = 4.dp,
            backgroundColor = MaterialTheme.colors.surface
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).background(MaterialTheme.colors.surface, shape = CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSurface
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
        modifier = Modifier.fillMaxWidth().height(400.dp)
    ) {
        items(4) { index ->
            when (index) {
                0 -> ProductCard(navController, R.drawable.ac1, "CeraVe Acne Control Gel", "Targets acne with 2% Salicylic Acid", "300 EGP", "com/example/hellofigma/facediseases/acne/acne.kt")
                1 -> ProductCard(navController, R.drawable.ec1, "Eucerin Eczema Relief", "Soothes eczema with Ceramide-3", "450 EGP", "com/example/hellofigma/facediseases/eczema/EczemaPage.kt")
                2 -> ProductCard(navController, R.drawable.eb1, "Cetaphil Hydrating Eye Gel", "Hydrates and brightens under-eye", "250 EGP", "com/example/hellofigma/facediseases/darkspots/DarkEyeSpotsPage.kt")
                3 -> ProductCard(navController, R.drawable.wr1, "Olay Regenerist Cream", "Reduces wrinkles with Niacinamide", "400 EGP", "com/example/hellofigma/facediseases/wrinkles/WrinklePage.kt")
            }
        }
    }
}

@Composable
fun ProductCard(navController: NavController, imageRes: Int, name: String, description: String, price: String, categoryRoute: String) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .width(160.dp)
            .height(240.dp)
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
                modifier = Modifier.size(100.dp).background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
            )
            Text(
                text = name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Cart",
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(32.dp).background(MaterialTheme.colors.primary, shape = RoundedCornerShape(8.dp)).padding(4.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(name, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary) },
            text = { Text(description, color = MaterialTheme.colors.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDialog = false; navController.navigate(categoryRoute) }) {
                    Text("View Category", color = MaterialTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close", color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.background
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MinimarketPreview() {
    Minimarket(navController = rememberNavController())
}
