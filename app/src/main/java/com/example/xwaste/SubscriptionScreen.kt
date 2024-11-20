package com.example.xwaste

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import java.util.*
import androidx.compose.foundation.clickable


@Composable
fun SubscriptionScreen() {
    var selectedPlan by remember { mutableStateOf("Monthly") }
    var paymentDate by remember { mutableStateOf("Select Date") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    val paymentMethods = listOf("MPesa", "Card")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Subscription Plan Selection
        Text("Subscription Plan:", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedPlan == "Monthly",
                onClick = { selectedPlan = "Monthly" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
            )
            Text("100 per month", fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedPlan == "Yearly",
                onClick = { selectedPlan = "Yearly" },
                colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
            )
            Text("1099 per year", fontSize = 16.sp)
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
                // Show Date Picker (Implementation to be added in logic)
                paymentDate = "07/13/2024" // Mock date for now
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Payment Method Dropdown
        Column {
            Text("Payment Method:", fontSize = 18.sp)
            Box {
                OutlinedTextField(
                    value = selectedPaymentMethod,
                    onValueChange = {},
                    label = { Text("Select a payment method") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Pay Button
        Button(
            onClick = {
                Toast.makeText(
                    null,
                    "Payment initiated with $selectedPlan plan and $selectedPaymentMethod",
                    Toast.LENGTH_LONG
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text("Pay", color = Color.White, fontSize = 18.sp)
        }
    }
}
