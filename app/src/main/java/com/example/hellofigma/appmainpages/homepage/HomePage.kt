package com.example.hellofigma.appmainpages.homepage

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.apptools.wishlist.WishlistItem
import com.example.hellofigma.apptools.wishlist.WishlistViewModel
import kotlinx.coroutines.launch

// ViewModel to persist cart count across navigation
class CartViewModel : ViewModel() {
    var cartCount by mutableStateOf(0)
        private set

    fun addItem() {
        cartCount++
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    wishlistViewModel: WishlistViewModel = viewModel(LocalContext.current as ComponentActivity),
    cartViewModel: CartViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hello (Zeyad Wael)",
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
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(onClick = { navController.navigate("com/example/hellofigma/apptools/wishlist/WishlistPage.kt") }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Wishlist")
                        }
                        if (cartViewModel.cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(18.dp)
                                    .background(Color.Red, shape = RoundedCornerShape(9.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cartViewModel.cartCount}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFBFBFB)
                )
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Best Selling Products",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ProductGrid(
                navController,
                wishlistViewModel,
                cartViewModel,
                modifier = Modifier.weight(1f),
                context,
                coroutineScope
            )
            Spacer(modifier = Modifier.height(16.dp))
            SeeMoreButton(navController)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductGrid(
    navController: NavController,
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier,
    context: android.content.Context,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(6) { index ->
            ProductCard(
                navController,
                wishlistViewModel,
                cartViewModel,
                productId = index,
                context,
                coroutineScope
            )
        }
    }
}

@Composable
fun ProductCard(
    navController: NavController,
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel,
    productId: Int,
    context: android.content.Context,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    var showDialog by remember { mutableStateOf(false) }

    val images = listOf(
        R.drawable.randomproduct1,
        R.drawable.randomproduct2,
        R.drawable.randomproduct3,
        R.drawable.randomproduct4,
        R.drawable.randomproduct5,
        R.drawable.randomproduct6
    )
    val productNames = listOf(
        "BOBAI Sunscreen Gel",
        "SHAAN BodyMilk",
        "GLAMY LAB Hydra Cream",
        "StarVille Micellar Water",
        "SHAAN Soothing Gel",
        "SHAAN Cream"
    )
    val descriptions = listOf(
        "Protects & Lightens",
        "Hydrates Dry Skin",
        "Nourishes Skin",
        "Cleanses & Brightens",
        "Deep Moisturizer",
        "B5 Protection"
    )
    val prices = listOf(
        "250 EGP",
        "350 EGP",
        "300 EGP",
        "200 EGP",
        "280 EGP",
        "320 EGP"
    )

    val imageRes = images[productId % images.size]
    val productName = productNames[productId % productNames.size]
    val description = descriptions[productId % descriptions.size]
    val price = prices[productId % prices.size]

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
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
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = productName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF7C7B7B),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = price,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add to Wishlist",
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF4741A6), shape = RoundedCornerShape(8.dp))
                    .clickable {
                        val priceValue = price.replace(" EGP", "").toDoubleOrNull() ?: 0.0
                        wishlistViewModel.addToWishlist(
                            WishlistItem(
                                name = productName,
                                category = description,
                                price = priceValue
                            )
                        )
                        cartViewModel.addItem() // increment cart count
                        coroutineScope.launch {
                            Toast.makeText(context, "$productName added to wishlist", Toast.LENGTH_SHORT).show()
                        }
                    },
                tint = Color.White
            )
        }
    }
}

@Composable
fun SeeMoreButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("com/example/hellofigma/appmainpages/minimarket/MiniMarketPage.kt") },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFF4741A6)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4741A6),
            contentColor = Color.White
        ),
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
fun HomePreview() {
    MaterialTheme {
        Home(navController = rememberNavController())
    }
}
