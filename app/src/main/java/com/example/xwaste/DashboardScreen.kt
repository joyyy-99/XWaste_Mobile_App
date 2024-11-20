package com.example.xwaste

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    val functionalities = listOf(
        Triple("Register Household", R.drawable.household, "register"),
        Triple("Get Garbage Bins", R.drawable.garbage_bins, "bins"),
        Triple("Start Subscription", R.drawable.subscribe, "subscribe"),
        Triple("Scheduling", R.drawable.schedule, "schedule"),
        Triple("Payment", R.drawable.payment, "payment"),
        Triple("Feedback", R.drawable.feedback, "feedback")
    )

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "XWaste Dashboard",
                style = MaterialTheme.typography.headlineSmall
            )
            Image(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                modifier = Modifier
                    .size(42.dp)
                    .clickable {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
            )
        }

        // Grid of functionalities
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(functionalities) { (label, imageResId, route) ->
                FunctionalityCard(
                    label = label,
                    imageResId = imageResId,
                    onClick = { onNavigate(route) }
                )
            }
        }
    }
}

@Composable
fun FunctionalityCard(label: String, imageResId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}
