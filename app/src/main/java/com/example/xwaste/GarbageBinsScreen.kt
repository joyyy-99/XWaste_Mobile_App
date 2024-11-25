package com.example.xwaste

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarbageBinsScreen(onNavigate: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var deliveryAddress by remember { mutableStateOf("") }
    var isOrganicSelected by remember { mutableStateOf(false) }
    var isRecyclableSelected by remember { mutableStateOf(false) }
    var isNonRecyclableSelected by remember { mutableStateOf(false) }

    // Fetch last delivery address from the database
    LaunchedEffect(Unit) {
        fetchLastDeliveryAddress { lastAddress ->
            deliveryAddress = lastAddress
        }
    }

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
                        .padding(16.dp)
                        .background(Color.White),
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
                        textStyle = TextStyle(color = Color.Black),
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

                    // Garbage Bin Options
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GarbageBinRow(
                            isSelected = isOrganicSelected,
                            imageResId = R.drawable.organic_waste,
                            label = "Organic Waste",
                            onToggle = { isOrganicSelected = it }
                        )
                        GarbageBinRow(
                            isSelected = isRecyclableSelected,
                            imageResId = R.drawable.recyclable_waste,
                            label = "Recyclable Waste",
                            onToggle = { isRecyclableSelected = it }
                        )
                        GarbageBinRow(
                            isSelected = isNonRecyclableSelected,
                            imageResId = R.drawable.non_recyclable_waste,
                            label = "Non-Recyclable Waste",
                            onToggle = { isNonRecyclableSelected = it }
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            saveSelectedGarbageBins(
                                deliveryAddress,
                                isOrganicSelected,
                                isRecyclableSelected,
                                isNonRecyclableSelected,
                                context
                            )
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
        )
    }
}


@Composable
fun GarbageBinRow(
    isSelected: Boolean,
    imageResId: Int,
    label: String,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .toggleable(
                value = isSelected,
                onValueChange = { onToggle(it) }
            )
    ) {
        // Checkbox
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle(it) }
        )

        // Garbage Bin Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = label,
            modifier = Modifier
                .size(80.dp)
                .padding(horizontal = 16.dp)
        )

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Helper function to fetch the last delivery address from Firebase
fun fetchLastDeliveryAddress(onResult: (String) -> Unit) {
    val databaseReference = FirebaseDatabase.getInstance("https://xwaste123-default-rtdb.firebaseio.com/")
        .getReference("households")

    databaseReference.orderByKey().limitToLast(1).get()
        .addOnSuccessListener { snapshot ->
            val lastEntry = snapshot.children.lastOrNull()
            val lastAddress = lastEntry?.child("location")?.value as? String ?: ""
            onResult(lastAddress)
        }
        .addOnFailureListener {
            onResult("")
        }
}

// Helper function to save selected garbage bins and address to Firebase
fun saveSelectedGarbageBins(
    deliveryAddress: String,
    isOrganicSelected: Boolean,
    isRecyclableSelected: Boolean,
    isNonRecyclableSelected: Boolean,
    context: android.content.Context
) {
    val databaseReference = FirebaseDatabase.getInstance("https://xwaste123-default-rtdb.firebaseio.com/")
        .getReference("garbageBins")

    val garbageBinData = mapOf(
        "deliveryAddress" to deliveryAddress,
        "organic" to isOrganicSelected,
        "recyclable" to isRecyclableSelected,
        "nonRecyclable" to isNonRecyclableSelected,
        "timestamp" to System.currentTimeMillis()
    )

    databaseReference.push().setValue(garbageBinData)
        .addOnSuccessListener {
            Toast.makeText(context, "Garbage bins selection saved!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { error ->
            Toast.makeText(context, "Failed to save selection: ${error.message}", Toast.LENGTH_SHORT).show()
        }
}