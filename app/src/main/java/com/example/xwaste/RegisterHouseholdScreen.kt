package com.example.xwaste

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterHouseholdScreen(onNavigate: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var householdName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCoordinates by remember { mutableStateOf<GeoPoint?>(null) }
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
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
                        .background(Color(0xFFF5F5F5)),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Register Household",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Household Name Input
                    OutlinedTextField(
                        value = householdName,
                        onValueChange = { householdName = it },
                        label = { Text("Household Name") },
                        textStyle = TextStyle(color = Color.Black),
                        placeholder = { Text("e.g. Kamau's Household") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Location Input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { address ->
                            location = address
                            if (address.isNotEmpty()) {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses: List<Address>? = geocoder.getFromLocationName(address, 1)
                                if (!addresses.isNullOrEmpty()) {
                                    val addressLocation = addresses[0]
                                    selectedCoordinates = GeoPoint(addressLocation.latitude, addressLocation.longitude)
                                    mapViewState.value?.let { map ->
                                        updateMapMarker(map, selectedCoordinates!!)
                                    }
                                } else {
                                    Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        placeholder = { Text("Type your address or select on map") },
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // MapView Integration
                    AndroidView(
                        factory = { ctx ->
                            val mapView = MapView(ctx)
                            mapViewState.value = mapView
                            mapView.setTileSource(TileSourceFactory.MAPNIK)
                            mapView.setMultiTouchControls(true)
                            mapView.controller.setZoom(15.0)
                            mapView.controller.setCenter(GeoPoint(-1.286389, 36.817223)) // Default to Nairobi

                            mapView.overlays.add(
                                MapEventsOverlay(object : MapEventsReceiver {
                                    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                        p?.let {
                                            selectedCoordinates = it
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            val addresses: List<Address>? = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                                            if (!addresses.isNullOrEmpty()) {
                                                location = addresses[0].getAddressLine(0)
                                            }
                                            updateMapMarker(mapView, it)
                                        }
                                        return true
                                    }

                                    override fun longPressHelper(p: GeoPoint?): Boolean {
                                        return false
                                    }
                                })
                            )
                            mapView
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Gray)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register Button
                    Button(
                        onClick = {
                            if (householdName.isNotEmpty() && selectedCoordinates != null) {
                                saveHouseholdToDatabase(householdName, location, selectedCoordinates, context)
                            } else {
                                Toast.makeText(context, "Please fill out all fields and select a location", Toast.LENGTH_SHORT).show()
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
                        Text("Register")
                    }
                }
            }
        )
    }
}


// Helper function to update the map marker
private fun updateMapMarker(mapView: MapView, geoPoint: GeoPoint) {
    mapView.overlays.clear() // Clear previous markers
    val marker = Marker(mapView)
    marker.position = geoPoint
    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    mapView.overlays.add(marker)
    mapView.invalidate()
}

// Save Household to Firebase Realtime Database
private fun saveHouseholdToDatabase(
    householdName: String,
    location: String,
    coordinates: GeoPoint?,
    context: Context
) {
    val database = FirebaseDatabase.getInstance().reference
    val householdId = database.child("households").push().key ?: return

    val householdData = mapOf(
        "householdName" to householdName,
        "location" to location,
        "latitude" to coordinates?.latitude,
        "longitude" to coordinates?.longitude
    )

    database.child("households").child(householdId).setValue(householdData)
        .addOnSuccessListener {
            Toast.makeText(context, "Household registered successfully!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to register household: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}