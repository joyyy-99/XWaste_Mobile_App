package com.example.xwaste

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale


@Composable
fun GarbageBinsScreen() {
    var deliveryAddress by remember { mutableStateOf("") }
    var isOrganicSelected by remember { mutableStateOf(false) }
    var isRecyclableSelected by remember { mutableStateOf(false) }
    var isNonRecyclableSelected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Select Garbage Bins",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Delivery Address Input
        OutlinedTextField(
            value = deliveryAddress,
            onValueChange = { deliveryAddress = it },
            label = { Text("Delivery Address") },
            placeholder = { Text("Enter your delivery address") },
            modifier = Modifier.fillMaxWidth()
        )

        // Instructions
        Text(
            text = "Select the garbage bins you want:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Garbage Bin Options with Checkboxes
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GarbageBinOptionWithCheckbox(
                    isSelected = isOrganicSelected,
                    imageResId = R.drawable.organic_waste,
                    label = "Organic Waste",
                    onToggle = { isOrganicSelected = it }
                )

                GarbageBinOptionWithCheckbox(
                    isSelected = isRecyclableSelected,
                    imageResId = R.drawable.recyclable_waste,
                    label = "Recyclable Waste",
                    onToggle = { isRecyclableSelected = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                GarbageBinOptionWithCheckbox(
                    isSelected = isNonRecyclableSelected,
                    imageResId = R.drawable.non_recyclable_waste,
                    label = "Non-Recyclable Waste",
                    onToggle = { isNonRecyclableSelected = it }
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                // Placeholder for functionality
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun GarbageBinOptionWithCheckbox(
    isSelected: Boolean,
    imageResId: Int,
    label: String,
    onToggle: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = { onToggle(it) }
            )
            .padding(horizontal = 8.dp)
    ) {
        // Checkbox and Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle(it) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Garbage Bin Image
        Image(
            painter = rememberAsyncImagePainter(imageResId),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
    }
}
