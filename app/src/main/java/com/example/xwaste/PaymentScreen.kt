package com.example.xwaste

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
fun PaymentScreen() {
    var selectedPlan by remember { mutableStateOf("Monthly") }
    var paymentDate by remember { mutableStateOf("Select Date") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    val paymentMethods = listOf("Pay with Card", "Pay with Cash")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Make a Payment",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Subscription Plan
        Text("Subscription Plan:", fontSize = 16.sp)
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPlan == "Monthly",
                    onClick = { selectedPlan = "Monthly" },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text(text = "100 per month", fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPlan == "Yearly",
                    onClick = { selectedPlan = "Yearly" },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text(text = "1099 per year", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Date Picker
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = paymentDate,
                onValueChange = {},
                label = { Text("Payment Date") },
                enabled = false,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                // Mock date selection for now
                paymentDate = "07/09/2024"
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Dropdown
        Text("Payment Method:", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box {
            OutlinedTextField(
                value = selectedPaymentMethod,
                onValueChange = {},
                label = { Text("Select a payment method") },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        Modifier.clickable { expanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                paymentMethods.forEach { method ->
                    DropdownMenuItem(onClick = {
                        selectedPaymentMethod = method
                        expanded = false
                    }) {
                        Text(text = method)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Pay Button
        Button(
            onClick = {
                // Handle payment logic here
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text(text = "Pay", color = Color.White, fontSize = 16.sp)
        }
    }
}
