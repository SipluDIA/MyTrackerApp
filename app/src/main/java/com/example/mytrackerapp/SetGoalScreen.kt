package com.example.mytrackerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetGoalScreen(
    navController: NavHostController,
    apiHelper: APIHelper,
    sessionManager: SessionManager
) {
    var walking by remember { mutableStateOf("") }
    var running by remember { mutableStateOf("") }
    var swimming by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Get current month name
    val currentMonth = remember {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Monthly Goals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentMonth,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Set your fitness targets",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Walking Goal Input
            GoalInputCard(
                icon = Icons.Default.DirectionsWalk,
                title = "Walking",
                value = walking,
                onValueChange = { walking = it },
                color = Color(0xFF4CAF50),
                unit = "steps"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Running Goal Input
            GoalInputCard(
                icon = Icons.Default.DirectionsRun,
                title = "Running",
                value = running,
                onValueChange = { running = it },
                color = Color(0xFFFF9800),
                unit = "km"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Swimming Goal Input
            GoalInputCard(
                icon = Icons.Default.Pool,
                title = "Swimming",
                value = swimming,
                onValueChange = { swimming = it },
                color = Color(0xFF2196F3),
                unit = "laps"
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
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
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    errorMessage = ""

                    // Validate inputs
                    val walkingValue = walking.toIntOrNull()
                    val runningValue = running.toIntOrNull()
                    val swimmingValue = swimming.toIntOrNull()

                    when {
                        walking.isEmpty() && running.isEmpty() && swimming.isEmpty() -> {
                            errorMessage = "Please set at least one goal"
                        }
                        walkingValue == null && walking.isNotEmpty() -> {
                            errorMessage = "Walking must be a valid number"
                        }
                        runningValue == null && running.isNotEmpty() -> {
                            errorMessage = "Running must be a valid number"
                        }
                        swimmingValue == null && swimming.isNotEmpty() -> {
                            errorMessage = "Swimming must be a valid number"
                        }
                        (walkingValue ?: 0) < 0 || (runningValue ?: 0) < 0 || (swimmingValue ?: 0) < 0 -> {
                            errorMessage = "Goals cannot be negative"
                        }
                        else -> {
                            isLoading = true
                            val userId = sessionManager.getUserId()

                            apiHelper.setGoal(
                                userId = userId,
                                walking = walkingValue ?: 0,
                                running = runningValue ?: 0,
                                swimming = swimmingValue ?: 0,
                                onSuccess = { message ->
                                    isLoading = false
                                    successMessage = message
                                    showSuccessDialog = true
                                },
                                onError = { error ->
                                    isLoading = false
                                    errorMessage = error
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Goals", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = { Text("Success!") },
                text = { Text(successMessage) },
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
fun GoalInputCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    unit: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Enter target $unit",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.width(120.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                placeholder = { Text("0") },
                suffix = { Text(unit, fontSize = 12.sp) }
            )
        }
    }
}