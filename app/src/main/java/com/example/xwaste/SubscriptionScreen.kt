package com.example.xwaste

import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(onNavigate: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("Monthly") }
    var paymentDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    val paymentMethods = listOf("Cash", "Card")

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
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.account),
                                contentDescription = "Account",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Subscription Plan
                    Text(
                        "Subscription Plan:",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
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

                    // Payment Date Field
                    OutlinedTextField(
                        value = paymentDate,
                        onValueChange = {},
                        label = { Text("Payment Date") },
                        placeholder = { Text("Select a date") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                customShowDatePicker(context) { date -> paymentDate = date }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select Date"
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.Black
                        )
                    )

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
                                    DropdownMenuItem(
                                        text = { Text(text = method) }, // Use `text` parameter explicitly
                                        onClick = {
                                            selectedPaymentMethod = method
                                            expanded = false
                                        }
                                    )
                                }
                            }


                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Pay Button
                    Button(
                        onClick = {
                            if (paymentDate.isBlank() || selectedPaymentMethod.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please complete all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                saveSubscriptionToFirebase(
                                    selectedPlan,
                                    paymentDate,
                                    selectedPaymentMethod
                                )
                                if (selectedPaymentMethod == "Card") {
                                    onNavigate("payment")
                                } else {
                                    Toast.makeText(
                                        context,
                                        "You will pay by cash when the waste is collected.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Pay", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        )
    }
}

fun customShowDatePicker(context: android.content.Context, onDateSelected: (String) -> Unit) {
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

fun saveSubscriptionToFirebase(plan: String, date: String, paymentMethod: String) {
    val database = FirebaseDatabase.getInstance("https://xwaste123-default-rtdb.firebaseio.com/")
    val subscriptionsRef = database.getReference("subscriptions")

    val newEntryRef = subscriptionsRef.push()
    val data = mapOf(
        "plan" to plan,
        "paymentDate" to date,
        "paymentMethod" to paymentMethod,
        "timestamp" to System.currentTimeMillis()
    )

    newEntryRef.setValue(data)
        .addOnSuccessListener {
            println("Subscription saved successfully")
        }
        .addOnFailureListener { e ->
            println("Error saving subscription: ${e.message}")
        }
}
