package com.example.mytrackerapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavHostController,
    apiHelper: APIHelper,
    sessionManager: SessionManager
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedActivity by remember { mutableStateOf("Walking") }
    var isTracking by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var endTime by remember { mutableStateOf<Long?>(null) }
    var startLocation by remember { mutableStateOf<Location?>(null) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var endLocation by remember { mutableStateOf<Location?>(null) }
    var locationPath by remember { mutableStateOf<MutableList<GeoPoint>>(mutableListOf()) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var totalDistance by remember { mutableStateOf(0.0) }
    var previousLocation by remember { mutableStateOf<Location?>(null) }

    // Location callback for live updates
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLocation = location
                    // Add to path if tracking
                    if (isTracking) {
                        locationPath.add(GeoPoint(location.latitude, location.longitude))
                        
                        // Calculate incremental distance and accumulate
                        previousLocation?.let { prev ->
                            val incrementalDistance = calculateDistance(prev, location)
                            totalDistance += incrementalDistance
                        }
                        
                        // Update previous location for next calculation
                        previousLocation = location
                    }
                }
            }
        }
    }

    // Initialize osmdroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Check permissions on start
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Start/Stop location updates
    DisposableEffect(isTracking) {
        if (isTracking && hasLocationPermission) {
            startLocationUpdates(fusedLocationClient, locationCallback)
        }

        onDispose {
            if (isTracking) {
                stopLocationUpdates(fusedLocationClient, locationCallback)
            }
        }
    }

    // Timer effect
    LaunchedEffect(isTracking, startTime) {
        if (isTracking && startTime != null) {
            while (isTracking) {
                elapsedTime = System.currentTimeMillis() - startTime!!
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Activity") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Activity Type Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Activity Type",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ActivityTypeOption(
                        icon = Icons.Default.DirectionsWalk,
                        text = "Walking",
                        selected = selectedActivity == "Walking",
                        onSelect = { if (!isTracking) selectedActivity = "Walking" },
                        color = Color(0xFF4CAF50)
                    )
                    ActivityTypeOption(
                        icon = Icons.Default.DirectionsRun,
                        text = "Running",
                        selected = selectedActivity == "Running",
                        onSelect = { if (!isTracking) selectedActivity = "Running" },
                        color = Color(0xFFFF9800)
                    )
                    ActivityTypeOption(
                        icon = Icons.Default.Pool,
                        text = "Swimming",
                        selected = selectedActivity == "Swimming",
                        onSelect = { if (!isTracking) selectedActivity = "Swimming" },
                        color = Color(0xFF2196F3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isTracking) "Activity in Progress" else "Ready to Start",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatTime(elapsedTime),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTracking) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location Info
            if (startLocation != null || currentLocation != null || endLocation != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (startLocation != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlagCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Start Location", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        "${String.format("%.6f", startLocation!!.latitude)}, ${String.format("%.6f", startLocation!!.longitude)}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (isTracking && currentLocation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF2196F3))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Current Location", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        "${String.format("%.6f", currentLocation!!.latitude)}, ${String.format("%.6f", currentLocation!!.longitude)}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (endLocation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFF44336))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("End Location", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        "${String.format("%.6f", endLocation!!.latitude)}, ${String.format("%.6f", endLocation!!.longitude)}",
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        if (isTracking || endLocation != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Straight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Distance", fontSize = 12.sp, color = Color.Gray)
                                    Text("${String.format("%.2f", totalDistance)} meters", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // OpenStreetMap View
            if (startLocation != null || currentLocation != null || endLocation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                mapView = this

                                // Initial setup
                                controller.setZoom(16.0)
                                startLocation?.let {
                                    controller.setCenter(GeoPoint(it.latitude, it.longitude))
                                }
                            }
                        },
                        update = { map ->
                            map.overlays.clear()

                            // Add start marker
                            startLocation?.let { start ->
                                val startMarker = Marker(map)
                                startMarker.position = GeoPoint(start.latitude, start.longitude)
                                startMarker.title = "Start"
                                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                map.overlays.add(startMarker)
                            }

                            // Add current location marker (blue dot) when tracking
                            if (isTracking && currentLocation != null) {
                                val currentMarker = Marker(map)
                                currentMarker.position = GeoPoint(currentLocation!!.latitude, currentLocation!!.longitude)
                                currentMarker.title = "Current"
                                currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                // Make it blue and smaller
                                currentMarker.icon = context.getDrawable(android.R.drawable.presence_online)
                                map.overlays.add(currentMarker)

                                // Center on current location while tracking
                                map.controller.animateTo(GeoPoint(currentLocation!!.latitude, currentLocation!!.longitude))
                            }

                            // Add end marker
                            if (endLocation != null) {
                                val endMarker = Marker(map)
                                endMarker.position = GeoPoint(endLocation!!.latitude, endLocation!!.longitude)
                                endMarker.title = "End"
                                endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                map.overlays.add(endMarker)
                            }

                            // Add path line
                            if (locationPath.isNotEmpty() && locationPath.size > 1) {
                                val line = Polyline(map)
                                locationPath.forEach { point ->
                                    line.addPoint(point)
                                }
                                line.color = android.graphics.Color.BLUE
                                line.width = 8f
                                map.overlays.add(line)
                            } else if (startLocation != null && endLocation != null) {
                                // Fallback: simple line from start to end
                                val line = Polyline(map)
                                line.addPoint(GeoPoint(startLocation!!.latitude, startLocation!!.longitude))
                                line.addPoint(GeoPoint(endLocation!!.latitude, endLocation!!.longitude))
                                line.color = android.graphics.Color.BLUE
                                line.width = 8f
                                map.overlays.add(line)
                            }

                            map.invalidate()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Control Buttons
            if (!isTracking && startTime == null) {
                // Start Button
                Button(
                    onClick = {
                        if (!hasLocationPermission) {
                            errorMessage = "Location permission required"
                            return@Button
                        }
                        getLocation(fusedLocationClient) { location ->
                            if (location != null) {
                                isTracking = true
                                startTime = System.currentTimeMillis()
                                startLocation = location
                                currentLocation = location
                                previousLocation = location
                                totalDistance = 0.0
                                locationPath.clear()
                                locationPath.add(GeoPoint(location.latitude, location.longitude))
                                errorMessage = ""
                            } else {
                                errorMessage = "Could not get location"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isTracking) {
                // Stop Button
                Button(
                    onClick = {
                        // Stop location updates
                        stopLocationUpdates(fusedLocationClient, locationCallback)

                        // Get final location
                        getLocation(fusedLocationClient) { location ->
                            isTracking = false
                            endTime = System.currentTimeMillis()
                            if (location != null) {
                                endLocation = location
                                locationPath.add(GeoPoint(location.latitude, location.longitude))
                                errorMessage = ""
                            } else {
                                // Use last current location as end
                                currentLocation?.let {
                                    endLocation = it
                                    errorMessage = ""
                                } ?: run {
                                    errorMessage = "Could not get final location"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stop Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else if (!isTracking && startTime != null && endTime != null) {
                // Save and Reset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Reset
                            startTime = null
                            endTime = null
                            startLocation = null
                            currentLocation = null
                            endLocation = null
                            locationPath.clear()
                            elapsedTime = 0L
                            totalDistance = 0.0
                            previousLocation = null
                            errorMessage = ""
                            mapView?.overlays?.clear()
                            mapView?.invalidate()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset")
                    }

                    Button(
                        onClick = {
                            if (startLocation != null && endLocation != null && startTime != null && endTime != null) {
                                isSaving = true
                                val userId = sessionManager.getUserId()
                                val duration = ((endTime!! - startTime!!) / 1000).toInt()

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val startTimeStr = dateFormat.format(Date(startTime!!))
                                val endTimeStr = dateFormat.format(Date(endTime!!))

                                apiHelper.saveActivity(
                                    userId = userId,
                                    activityType = selectedActivity,
                                    startTime = startTimeStr,
                                    endTime = endTimeStr,
                                    duration = duration,
                                    startLatitude = startLocation!!.latitude,
                                    startLongitude = startLocation!!.longitude,
                                    endLatitude = endLocation!!.latitude,
                                    endLongitude = endLocation!!.longitude,
                                    distance = totalDistance,
                                    onSuccess = { message, _ ->
                                        isSaving = false
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
                                        isSaving = false
                                        errorMessage = error
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save")
                        }
                    }
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                },
                title = { Text("Activity Saved!") },
                text = { Text("Your $selectedActivity activity has been saved successfully.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ActivityTypeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    selected: Boolean,
    onSelect: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onSelect)
    }
}

fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = (milliseconds / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@SuppressLint("MissingPermission")
fun getLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        onLocationReceived(location)
    }.addOnFailureListener {
        onLocationReceived(null)
    }
}

@SuppressLint("MissingPermission")
fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        2000 // Update every 2 seconds
    ).apply {
        setMinUpdateIntervalMillis(1000) // Fastest update: 1 second
        setMaxUpdateDelayMillis(5000)
    }.build()

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

fun stopLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
    fusedLocationClient.removeLocationUpdates(locationCallback)
}

fun calculateDistance(start: Location, end: Location): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(end.latitude - start.latitude)
    val dLon = Math.toRadians(end.longitude - start.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}