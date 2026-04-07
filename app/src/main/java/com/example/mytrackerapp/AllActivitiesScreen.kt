package com.example.mytrackerapp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllActivitiesScreen(
    navController: NavHostController,
    apiHelper: APIHelper,
    sessionManager: SessionManager
) {
    var activities by remember { mutableStateOf<List<ActivityData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Load activities when screen opens
    LaunchedEffect(Unit) {
        val userId = sessionManager.getUserId()
        apiHelper.viewActivities(
            userId = userId,
            onSuccess = { activitiesList ->
                activities = activitiesList
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    // Group activities by type
    val groupedActivities = activities.groupBy { it.activityType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Activities") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("activity") }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Activity",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.Yellow
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    // Loading State
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading activities...", fontSize = 16.sp, color = Color.Gray)
                    }
                }

                errorMessage.isNotEmpty() -> {
                    // Error State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = {
                            isLoading = true
                            errorMessage = ""
                            val userId = sessionManager.getUserId()
                            apiHelper.viewActivities(
                                userId = userId,
                                onSuccess = { activitiesList ->
                                    activities = activitiesList
                                    isLoading = false
                                },
                                onError = { error ->
                                    errorMessage = error
                                    isLoading = false
                                }
                            )
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }

                activities.isEmpty() -> {
                    // Empty State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.DirectionsRun,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Activities Yet",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Start tracking your fitness activities!",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { navController.navigate("activity") },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(56.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Track Activity")
                        }
                    }
                }

                else -> {
                    // Activities List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Summary Card
                        item {
                            SummaryCard(
                                totalActivities = activities.size,
                                totalDistance = activities.sumOf { it.distance },
                                totalDuration = activities.sumOf { it.duration },
                                totalCalories = activities.sumOf { calculateCalories(it.activityType, it.duration, it.distance) }
                            )
                        }

                        // Grouped Activities by Type
                        groupedActivities.forEach { (activityType, activitiesByType) ->
                            item {
                                ActivityTypeHeader(
                                    activityType = activityType,
                                    count = activitiesByType.size
                                )
                            }

                            items(activitiesByType) { activity ->
                                ActivityCard(activity = activity)
                            }
                        }

                        // Bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    totalActivities: Int,
    totalDistance: Double,
    totalDuration: Int,
    totalCalories: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "My Activity Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(
                    icon = Icons.Default.FitnessCenter,
                    value = totalActivities.toString(),
                    label = "Activities",
                    color = Color(0xFF4CAF50)
                )
                SummaryItem(
                    icon = Icons.Default.Leaderboard,
                    value = String.format("%.1f", totalDistance / 1000),
                    label = "km",
                    color = Color(0xFF2196F3)
                )
                SummaryItem(
                    icon = Icons.Default.Timer,
                    value = String.format("%.0f", totalDuration / 60.0),
                    label = "mins",
                    color = Color(0xFFFF9800)
                )
                SummaryItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = String.format("%.0f", totalCalories),
                    label = "kcal",
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ActivityTypeHeader(activityType: String, count: Int) {
    val (icon, color) = when (activityType) {
        "Walking" -> Icons.Default.DirectionsWalk to Color(0xFF4CAF50)
        "Running" -> Icons.Default.DirectionsRun to Color(0xFFFF9800)
        "Swimming" -> Icons.Default.Pool to Color(0xFF2196F3)
        else -> Icons.Default.FitnessCenter to Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            "$activityType ($count)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActivityCard(activity: ActivityData) {
    val (icon, color) = when (activity.activityType) {
        "Walking" -> Icons.Default.DirectionsWalk to Color(0xFF4CAF50)
        "Running" -> Icons.Default.DirectionsRun to Color(0xFFFF9800)
        "Swimming" -> Icons.Default.Pool to Color(0xFF2196F3)
        else -> Icons.Default.FitnessCenter to Color.Gray
    }

    val calories = calculateCalories(activity.activityType, activity.duration, activity.distance)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        activity.formattedDate,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Duration
                StatItem(
                    icon = Icons.Default.Timer,
                    label = "Duration",
                    value = formatDuration(activity.duration),
                    color = color
                )

                // Distance
                StatItem(
                    icon = Icons.Default.Leaderboard,
                    label = "Distance",
                    value = "${String.format("%.2f", activity.distance)} m",
                    color = color
                )

                // Calories
                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Calories",
                    value = "${String.format("%.0f", calories)} kcal",
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

fun formatDuration(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return when {
        hours > 0 -> String.format("%dh %dm", hours, minutes)
        minutes > 0 -> String.format("%dm %ds", minutes, secs)
        else -> String.format("%ds", secs)
    }
}

// Calculate calories based on activity type, duration (seconds), and distance (meters)
fun calculateCalories(activityType: String, duration: Int, distance: Double): Double {
    // MET (Metabolic Equivalent of Task) values
    val met = when (activityType) {
        "Walking" -> 3.5  // Moderate walking
        "Running" -> 9.8  // Running at 6 mph
        "Swimming" -> 6.0 // Moderate swimming
        else -> 3.0
    }

    // Average weight in kg (can be customized based on user profile)
    val weightKg = 70.0

    // Calories = MET × weight(kg) × duration(hours)
    val durationHours = duration / 3600.0
    return met * weightKg * durationHours
}


