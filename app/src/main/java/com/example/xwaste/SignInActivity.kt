package com.example.xwaste

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xwaste.ui.theme.XWasteTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.focus.*
import androidx.compose.ui.text.input.VisualTransformation

class SignInActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            XWasteTheme {
                SignInScreen(
                    onLoginClick = { email, password -> loginWithEmail(email, password) },
                    onRegisterClick = { username, email, phone, password ->
                        registerWithEmail(username, email, phone, password)
                    },
                    onGoogleSignInClick = { signInWithGoogle() }
                )
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateToDashboard()
                } else {
                    showToast("Login failed: ${task.exception?.message}")
                }
            }
    }

    private fun registerWithEmail(username: String, email: String, phone: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    val database = FirebaseDatabase.getInstance().reference

                    // Create user data map
                    val user = mapOf(
                        "username" to username,
                        "email" to email,
                        "phone" to phone
                    )

                    // Save user details in the "users" table
                    database.child("users").child(userId).setValue(user)
                        .addOnSuccessListener {
                            showToast("Registration successful!")
                            navigateToDashboard()
                        }
                        .addOnFailureListener {
                            showToast("Failed to save user data: ${it.message}")
                        }
                } else {
                    showToast("Registration failed: ${task.exception?.message}")
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account: GoogleSignInAccount? = task.result
            handleGoogleSignInResult(account)
        }
    }

    private fun handleGoogleSignInResult(account: GoogleSignInAccount?) {
        account?.let {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        navigateToDashboard()
                    } else {
                        showToast("Google Sign-In failed")
                    }
                }
        }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}

@Composable
fun SignInScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (String, String, String, String) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // White background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // XWaste Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your logo
                contentDescription = "XWaste Logo",
                modifier = Modifier
                    .size(80.dp) // Adjust as needed
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .background(Color.White),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Text(
                        text = if (isLoginMode) "Login" else "Register",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black // Text color
                    )

                    // Input Fields
                    if (!isLoginMode) {
                        CustomTextField(value = username, label = "Username") { username = it }
                        CustomTextField(value = phone, label = "Phone Number") { phone = it }
                    }
                    CustomTextField(value = email, label = "Email") { email = it }
                    CustomTextField(
                        value = password,
                        label = "Password",
                        isPassword = true
                    ) { password = it }

                    if (!isLoginMode) {
                        CustomTextField(
                            value = confirmPassword,
                            label = "Confirm Password",
                            isPassword = true
                        ) { confirmPassword = it }
                    }

                    // Buttons
                    Button(
                        onClick = {
                            if (isLoginMode) {
                                onLoginClick(email, password)
                            } else {
                                if (password == confirmPassword) {
                                    onRegisterClick(username, email, phone, password)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Passwords do not match",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White // White text
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLoginMode) "Login" else "Register")
                    }

                    // Toggle between Login/Register
                    TextButton(onClick = { isLoginMode = !isLoginMode }) {
                        Text(
                            if (isLoginMode) "Don't have an account? Register" else "Already have an account? Login",
                            color = Color.Black
                        )
                    }

                    if (isLoginMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray
                            )
                            Text(
                                text = "or",
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = Color.Gray
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color.Gray
                            )
                        }

                        // Google Sign-In Button
                        Button(
                            onClick = onGoogleSignInClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Sign in with Google",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom TextField with focus change colors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    label: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) Color.White else Color.White
    val textColor = if (isFocused) Color.Black else Color.Black

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = textColor) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
            .background(Color.White)
            .onFocusChanged { isFocused = it.isFocused },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = backgroundColor,
            focusedTextColor = textColor,
            unfocusedTextColor = Color.Black,
            focusedLabelColor = Color.LightGray,
            unfocusedLabelColor = Color.White,
            cursorColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),

    )
}
