
package com.example.hellofigma.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hellofigma.ui.theme.*
import com.example.hellofigma.ui.utils.Product

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SearchBarBackground),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { showDialog = true }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(product.imageRes),
                contentDescription = "Product Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(140.dp)
                    .background(SearchBarBackground)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = product.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(product.imageRes),
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(100.dp)
                                .background(SearchBarBackground)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = product.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Primary,
                            modifier = Modifier.weight(1f).padding(start = 12.dp)
                        )
                    }
                    Text(product.description, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextSecondary, lineHeight = 18.sp)
                    Text("Size: ${product.size}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BlackText)
                    Text("Use: ${product.usage}", fontSize = 12.sp, color = TextSecondary)
                    Text("Ingredients: ${product.ingredients}", fontSize = 12.sp, color = TextSecondary)
                    Text("${product.price} EGP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.align(Alignment.End))
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text("Close", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                }
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(White)
        )
    }
}
