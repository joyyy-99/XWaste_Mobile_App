package com.example.xwaste

import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun SchedulingScreen() {
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Schedule Pickup",
            style = MaterialTheme.typography.h5, // Material2 Typography
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Pickup Date Field
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { selectedDate = it },
            label = { Text("Pickup Date", color = Color.Black) },
            placeholder = { Text("Select a date", color = Color.Gray) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    showDatePicker(context) { date ->
                        selectedDate = date
                    }
                }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Select Date",
                        tint = Color.Black
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Black
            )
        )

        // Submit Button
        Button(
            onClick = {
                if (selectedDate.isNotBlank()) {
                    Toast.makeText(context, "Pickup scheduled for $selectedDate", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Submit")
        }
    }
}

// Helper function to show DatePickerDialog
fun showDatePicker(context: android.content.Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val formattedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
            onDateSelected(formattedDate)
        },
        year,
        month,
        day
    )
    datePickerDialog.show()
}
