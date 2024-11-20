package com.example.xwaste

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.xwaste.ui.theme.XWasteTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is signed in
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Redirect to SignInActivity if no user is signed in
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        // Set the app content
        setContent {
            XWasteTheme {
                XWasteApp()
            }
        }
    }
}

@Composable
fun XWasteApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable("register") {
            RegisterHouseholdScreen() // Display UI-only Register Household screen
        }
        composable("bins") { GarbageBinsScreen() }
        composable("subscribe") { SubscriptionScreen() }
        composable("schedule") { SchedulingScreen() }
        composable("payment") { PaymentScreen() }
        composable("feedback") { FeedbackScreen() }
    }
}
