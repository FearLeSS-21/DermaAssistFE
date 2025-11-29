package com.example.hellofigma.facediseases.darkspots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun Dark(navController: NavController, modifier: Modifier = Modifier) {

    val products = listOf(
        Product(R.drawable.eb1, "Glamy Lab Hydra Intense Cream", "Hydrates all skin types", "50g", "Apply twice daily", "Aloe Vera, Chamomile", Random.nextInt(200, 501)),
        Product(R.drawable.eb2, "Eucerin Eczema Relief Cream", "Reduces eczema inflammation", "226g", "Use on irritated areas", "Colloidal Oatmeal", Random.nextInt(200, 501)),
        Product(R.drawable.eb4, "Aveeno Eczema Moisturizing Cream", "Moisturizes eczema-prone skin", "73g", "Apply after cleansing", "Oat Extract", Random.nextInt(200, 501)),
        Product(R.drawable.eb6, "Elidel Cream", "Treats atopic dermatitis", "30g", "Use as directed by doctor", "Pimecrolimus", Random.nextInt(200, 501)),
        Product(R.drawable.eb7, "Tacrolimus Ointment", "For dermatologic use only", "30g", "Apply to affected areas", "Tacrolimus", Random.nextInt(200, 501)),
        Product(R.drawable.eb8, "SHAAN Cream", "Moisturizes sensitive skin", "453g", "Massage post-bath", "Glycerin, Petrolatum", Random.nextInt(200, 501)),
        Product(R.drawable.eb8, "StarVille Micellar Water", "Daily hydration for sensitive skin", "89ml", "Use morning and night", "Hyaluronic Acid, Ceramides", Random.nextInt(200, 501))
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Market", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Dark Spots", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)

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
            SeeMoreButton(navController)
            Spacer(Modifier.height(16.dp))
        }
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
    Dark(rememberNavController())
}
