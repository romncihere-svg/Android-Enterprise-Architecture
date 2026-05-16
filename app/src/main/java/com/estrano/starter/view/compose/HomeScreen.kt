package com.estrano.starter.view.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estrano.starter.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    featuredProducts: List<Product>,
    marketProducts: List<Product>,
    onProductClick: (Product) -> Unit
) {
    val categories = listOf("All", "Source Code", "Discord Bots", "Tools", "Templates", "Assets")
    var selectedCategory by remember { mutableStateOf("All") }
    var showSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (showSheet && selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E1E)
        ) {
            PaymentBottomSheetContent(
                product = selectedProduct!!,
                onConfirm = { 
                    showSheet = false
                    // Handle payment intent logic here or via callback
                },
                onDismiss = { showSheet = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estrano Stores", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0D0D0D)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Exclusive Content",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                "Featured Market",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF03DAC5),
                            selectedLabelColor = Color.Black,
                            labelColor = Color.Gray
                        ),
                        border = null,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured Products Horizontal
            Text("Featured", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(featuredProducts) { product ->
                    FeaturedProductCard(product) { 
                        selectedProduct = product
                        showSheet = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // All Products
            Text("Latest Marketplace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            marketProducts.forEach { product ->
                MarketProductCard(product) { 
                    selectedProduct = product
                    showSheet = true
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FeaturedProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    product.description,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("$${product.price}", color = Color(0xFF03DAC5), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MarketProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF03DAC5))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(product.category, color = Color.Gray, fontSize = 12.sp)
            }
            Text("$${product.price}", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
