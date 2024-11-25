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
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(onNavigate: (String) -> Unit) {
    val email = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val context = LocalContext.current

    // Firebase Database Reference
    val database: DatabaseReference = FirebaseDatabase
        .getInstance("https://xwaste123-default-rtdb.firebaseio.com/")
        .reference

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            SideMenu(onClose = { scope.launch { drawerState.close() } }, onNavigate = onNavigate)
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("XWaste", style = MaterialTheme.typography.titleMedium)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val intent = Intent(context, AccountActivity::class.java)
                            context.startActivity(intent) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.account),
                                contentDescription = "Account",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(context, SignInActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp),
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
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Gray,
                        ),
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
        )
    }
}

// Feedback Data Class
data class Feedback(
    val email: String = "",
    val message: String = ""
)
