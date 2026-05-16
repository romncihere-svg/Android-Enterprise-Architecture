package com.estrano.starter.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estrano.starter.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    allProducts: List<Product>,
    onProductClick: (Product) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val categories = listOf("All", "Cheats", "Source Code", "Discord Bots", "IMGUI Menus")

    val filteredProducts = allProducts.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        val matchesQuery = searchQuery.isEmpty() || 
                product.name.contains(searchQuery, ignoreCase = true) || 
                product.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    if (showSheet && selectedProduct != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E1E)
        ) {
            PaymentBottomSheetContent(
                product = selectedProduct!!,
                onConfirm = { showSheet = false },
                onDismiss = { showSheet = false }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("Marketplace", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Search products...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFF1E1E1E),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    shape = RoundedCornerShape(12.dp),
                    border = null
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredProducts) { product ->
                MarketProductCard(product = product) { 
                    selectedProduct = product
                    showSheet = true
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
