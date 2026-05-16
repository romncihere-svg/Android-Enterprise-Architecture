package com.estrano.starter.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.estrano.starter.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheetContent(
    product: Product,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(Color.Gray, RoundedCornerShape(2.dp))
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFF03DAC5), modifier = Modifier.size(48.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Checkout Details", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(product.name, color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Amount", color = Color.White)
            Text("$${product.price}", color = Color(0xFF03DAC5), fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Pay with UPI / Google Pay", color = Color.Black, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        TextButton(onClick = onDismiss) {
            Text("Cancel Order", color = Color.Gray)
        }
    }
}
