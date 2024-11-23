package com.example.xwaste

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FeedbackScreen() {
    val email = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val context = LocalContext.current

    // Firebase Database Reference
    val database: DatabaseReference = FirebaseDatabase
        .getInstance("https://xwaste123-default-rtdb.firebaseio.com/")
        .reference

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Submit Feedback",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Email Input Field
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            placeholder = { Text("Enter your email") },
            textStyle = TextStyle(color = Color.Black), // Set text color to black
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Message Input Field
        OutlinedTextField(
            value = message.value,
            onValueChange = { message.value = it },
            label = { Text("Message") },
            placeholder = { Text("Enter your feedback") },
            textStyle = TextStyle(color = Color.Black), // Set text color to black
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        // Submit Button
        Button(
            onClick = {
                val feedbackData = Feedback(email = email.value, message = message.value)

                if (email.value.isNotBlank() && message.value.isNotBlank()) {
                    database.child("Feedback").push().setValue(feedbackData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Feedback submitted successfully!", Toast.LENGTH_LONG).show()
                            email.value = ""
                            message.value = ""
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text("Submit")
        }
    }
}

// Feedback Data Class
data class Feedback(
    val email: String = "",
    val message: String = ""
)
