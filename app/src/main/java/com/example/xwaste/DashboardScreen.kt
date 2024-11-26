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
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // Navigate to Account Settings
                            val intent = Intent(context, AccountActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.account), // Replace with your account icon resource
                                contentDescription = "Account Settings",
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
                Column(modifier = Modifier.padding(paddingValues).background(Color(0xFFF5F5F5))) {
                    DashboardContent(onNavigate)
                }
            }
        )
    }
}

@Composable
fun DashboardContent(onNavigate: (String) -> Unit) {
    val functionalities = listOf(
        Functionality("Register Your Household", R.drawable.household, "Register your household to start receiving waste management services.", "Register Now", "register"),
        Functionality("Get Garbage Bins", R.drawable.garbage_bins, "Order your garbage bins for proper waste segregation.", "Get Bins", "bins"),
        Functionality("Start Subscription", R.drawable.subscribe, "Subscribe to waste management services.", "Start Now", "subscribe"),
        Functionality("Scheduling", R.drawable.schedule, "Schedule your waste collection.", "Schedule Now", "schedule"),
        Functionality("Payment", R.drawable.payment, "Pay for your waste management services.", "Pay Now", "payment"),
        Functionality("Feedback", R.drawable.feedback, "Provide feedback on our services.", "Give Feedback", "feedback")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(functionalities, key = { it.label }) { functionality -> // Use stable keys
            FunctionalityCard(functionality) {
                onNavigate(functionality.route)
            }
        }
    }

}

@Composable
fun SideMenu(onClose: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your logo resource
                contentDescription = "XWaste Logo",
                modifier = Modifier
                    .height(60.dp), // Adjust size as needed
                contentScale = ContentScale.Fit
            )
            Text("XWaste", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close Menu")
            }
        }
        HorizontalDivider()
        val menuItems = listOf(
            "Home" to "dashboard",
            "Scheduling" to "schedule",
            "Subscribe" to "subscribe",
            "Payment" to "payment",
            "Feedback" to "feedback",
        )
        menuItems.forEach { (label, route) ->
            SideMenuItem(label = label, onClick = {
                onNavigate(route)
                onClose()
            })
        }
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider()
        SideMenuItem(label = "Account", onClick = {
            val intent = Intent(context, AccountActivity::class.java)
            context.startActivity(intent) // Navigate to AccountActivity
            onClose()
        })
        SideMenuItem(label = "Logout", onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        })
    }
}

@Composable
fun SideMenuItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun FunctionalityCard(functionality: Functionality, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // Removed shadow for smoother scrolling
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = functionality.imageResId), // Optimized image loading
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop // Crop large images to fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = functionality.label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = functionality.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
            ) {
                Text(text = functionality.buttonText)
            }
        }
    }
}

data class Functionality(
    val label: String,
    val imageResId: Int,
    val description: String,
    val buttonText: String,
    val route: String
)
