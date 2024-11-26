package com.example.xwaste




class AccountActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setContent {
            XWasteTheme {
                AccountScreen(
                    firebaseAuth = firebaseAuth,
                    database = database,
                    onLogout = {
                        logoutUser()
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

    var updatedDetails = remember { mutableStateMapOf<String, String>() }

    userDetails.forEach { (key, value) ->
        if (!updatedDetails.containsKey(key)) {
            updatedDetails[key] = value
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            SideMenu(
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
                                .clickable { onLogout() }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black
                    )
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
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

