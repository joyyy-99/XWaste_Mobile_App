package com.example.xwaste

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xwaste.ui.theme.XWasteTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch


class AccountActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setContent {
            XWasteTheme { //Set  up the acconut screen with logout functionality
                AccountScreen(
                    firebaseAuth = firebaseAuth,
                    database = database,
                    onLogout = {
                        logoutUser() // Handle user logout
                    }
                )
            }
        }
    }

    private fun logoutUser() {
        firebaseAuth.signOut() // Sign out the user
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // End the current activity
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    firebaseAuth: FirebaseAuth,
    database: DatabaseReference,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val currentUser = firebaseAuth.currentUser
    var userDetails by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isEditing by remember { mutableStateOf(false) }

    // Fetch user details from Firebase Database
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userId = user.uid
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val details = mutableMapOf<String, String>()
                    for (child in snapshot.children) {
                        val key = child.key ?: "Unknown"
                        val value = child.getValue(String::class.java) ?: "N/A"
                        details[key] = value
                    }
                    userDetails = details
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AccountScreen", "Error fetching user data: ${error.message}")
                }
            })
        }
    }
    // Track updated user details locally for editing
    var updatedDetails = remember { mutableStateMapOf<String, String>() }
    // Initialize updatedDetails map with values from userDetails
    userDetails.forEach { (key, value) ->
        if (!updatedDetails.containsKey(key)) {
            updatedDetails[key] = value
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            SideMenu( //sidebar menu for navigation
                onClose = { scope.launch { drawerState.close() } },
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    when (route) {
                        "logout" -> onLogout()
                        else -> context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            )
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
                    // button to open nav drawer
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    //redirect to acountactivity when clicked
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
                                .clickable { onLogout() }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            // main content area
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)), // light gray background
                    contentAlignment = Alignment.Center
                ) {
                    // Column to display user details and action buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // header text indicating current mode
                        Text(
                            text = if (isEditing) "Edit Account Details" else "Account Details",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        // Display editable fields dynamically
                        userDetails.forEach { (key, value) ->
                            OutlinedTextField(
                                value = updatedDetails[key] ?: "",
                                onValueChange = { newValue -> updatedDetails[key] = newValue },
                                label = { Text(text = key.capitalize()) },
                                enabled = isEditing,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isEditing) {
                            // Save Button
                            Button(
                                onClick = {
                                    currentUser?.let { user ->
                                        val userId = user.uid
                                        database.child("users").child(userId).setValue(updatedDetails)
                                            .addOnSuccessListener {
                                                isEditing = false
                                                Log.d("AccountScreen", "User details updated successfully")
                                            }
                                            .addOnFailureListener { error ->
                                                Log.e("AccountScreen", "Error updating data: ${error.message}")
                                            }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF388E3C),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(text = "Save", fontSize = 18.sp)
                            }
                        } else {
                            // Edit Button
                            Button(
                                onClick = { isEditing = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1976D2),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(text = "Edit", fontSize = 18.sp)
                            }
                        }

                        // Logout Button
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(text = "Logout", fontSize = 18.sp)
                        }
                    }
                }
            }
        )
    }
}

