package com.example.mytrackerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    apiHelper: APIHelper,
    sessionManager: SessionManager
) {
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val username = sessionManager.getUsername()

    // Load profile data when screen opens
    LaunchedEffect(Unit) {
        val userId = sessionManager.getUserId()
        apiHelper.getProfile(
            userId = userId,
            onSuccess = { profile ->
                if (profile != null) {
                    age = profile.age.toString()
                    gender = profile.gender
                    weight = profile.weight.toString()
                    height = profile.height.toString()
                }
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.large
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = username,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Update your profile information",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Age Input
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    leadingIcon = {
                        Icon(Icons.Default.Cake, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("years") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Selection
                Text(
                    text = "Gender",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        GenderOption(
                            text = "Male",
                            selected = gender == "Male",
                            onSelect = { gender = "Male" }
                        )
                        GenderOption(
                            text = "Female",
                            selected = gender == "Female",
                            onSelect = { gender = "Female" }
                        )
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weight Input
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    leadingIcon = {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("kg") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Height Input
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height") },
                    leadingIcon = {
                        Icon(Icons.Default.Height, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    suffix = { Text("cm") }
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

                // BMI Card (if weight and height are available)
                if (weight.isNotEmpty() && height.isNotEmpty()) {
                    val weightValue = weight.toDoubleOrNull()
                    val heightValue = height.toDoubleOrNull()

                    if (weightValue != null && heightValue != null && heightValue > 0) {
                        val bmi = weightValue / ((heightValue / 100) * (heightValue / 100))
                        val bmiCategory = when {
                            bmi < 18.5 -> "Underweight"
                            bmi < 25 -> "Normal"
                            bmi < 30 -> "Overweight"
                            else -> "Obese"
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your BMI",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = String.format("%.1f", bmi),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = bmiCategory,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        errorMessage = ""

                        val ageValue = age.toIntOrNull()
                        val weightValue = weight.toDoubleOrNull()
                        val heightValue = height.toDoubleOrNull()

                        when {
                            age.isEmpty() || gender.isEmpty() || weight.isEmpty() || height.isEmpty() -> {
                                errorMessage = "Please fill all fields"
                            }
                            ageValue == null || ageValue <= 0 -> {
                                errorMessage = "Please enter a valid age"
                            }
                            weightValue == null || weightValue <= 0 -> {
                                errorMessage = "Please enter a valid weight"
                            }
                            heightValue == null || heightValue <= 0 -> {
                                errorMessage = "Please enter a valid height"
                            }
                            else -> {
                                isSaving = true
                                val userId = sessionManager.getUserId()

                                apiHelper.saveProfile(
                                    userId = userId,
                                    age = ageValue,
                                    gender = gender,
                                    weight = weightValue,
                                    height = heightValue,
                                    onSuccess = { message ->
                                        isSaving = false
                                        successMessage = message
                                        showSuccessDialog = true
                                    },
                                    onError = { error ->
                                        isSaving = false
                                        errorMessage = error
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Profile", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
fun GenderOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .selectable(
                selected = selected,
                onClick = onSelect
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp)
    }
}