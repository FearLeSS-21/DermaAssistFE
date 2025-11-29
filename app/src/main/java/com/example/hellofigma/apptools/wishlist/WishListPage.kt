package com.example.hellofigma.apptools.wishlist

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hellofigma.R
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.hellofigma.ui.theme.*   // Brings in Primary, White, TextSecondary, DeleteRed, GradientEnd

class WishlistViewModel : ViewModel() {
    val wishlistItems = mutableStateListOf<WishlistItem>()

    fun addToWishlist(item: WishlistItem) {
        if (!wishlistItems.contains(item)) {
            wishlistItems.add(item)
            println("Wishlist updated: Added ${item.name}, Total items: ${wishlistItems.size}")
        }
    }

    fun removeFromWishlist(item: WishlistItem) {
        wishlistItems.remove(item)
        println("Wishlist updated: Removed ${item.name}, Total items: ${wishlistItems.size}")
    }
}

data class WishlistItem(val name: String, val category: String, val price: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wishlist(navController: NavController, viewModel: WishlistViewModel = viewModel(LocalContext.current as ComponentActivity)) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Your Wishlist",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            if (viewModel.wishlistItems.isEmpty()) {
                EmptyWishlistState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.wishlistItems, key = { it.name }) { item ->
                        WishlistCard(item = item, onRemove = { viewModel.removeFromWishlist(item) })
                    }
                }
                ProceedToPaymentButton()
            }
        }
    }
}

@Composable
fun EmptyWishlistState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Empty Wishlist",
            tint = TextSecondary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Wishlist is Empty",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Add some products to get started!",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun WishlistCard(item: WishlistItem, onRemove: () -> Unit) {
    val imageMap = mapOf(
        "BOBAI Sunscreen Gel" to R.drawable.randomproduct1,
        "SHAAN BodyMilk" to R.drawable.randomproduct2,
        "GLAMY LAB Hydra Cream" to R.drawable.randomproduct3,
        "StarVille Micellar Water" to R.drawable.randomproduct4,
        "SHAAN Soothing Gel" to R.drawable.randomproduct5,
        "SHAAN Cream" to R.drawable.randomproduct6
    )
    val imageRes = imageMap[item.name] ?: R.drawable.randomproduct1

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
        exit = fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = "${item.name} Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SearchBarBackground) // light gray behind image
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "${item.price} EGP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BlackText,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = DeleteRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProceedToPaymentButton() {
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(initialScale = 0.8f) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut()
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Primary, GradientEnd)
                    )
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Text(
                text = "Proceed to Payment",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center
            )
        }
    }
}