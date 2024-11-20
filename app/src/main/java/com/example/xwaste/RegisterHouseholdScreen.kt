package com.example.xwaste

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RegisterHouseholdScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Register Household",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Household Name Input
        OutlinedTextField(
            value = "",
            onValueChange = {}, // No action
            label = { Text("Household Name") },
            placeholder = { Text("e.g. Kamau's Household") },
            modifier = Modifier.fillMaxWidth()
        )

        // Location Input
        OutlinedTextField(
            value = "",
            onValueChange = {}, // No action
            label = { Text("Location") },
            placeholder = { Text("Type or select from the map") },
            modifier = Modifier.fillMaxWidth()
        )

        // Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Map Placeholder",
                color = Color.White
            )
        }

        // Register Button
        Button(
            onClick = {}, // No action
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Register")
        }
    }
}
