package com.example.mytrackerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle // For the checkmark icon
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ExitToApp   // For the logout icon
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HouseSiding
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mytrackerapp.ui.theme.Black
import com.example.mytrackerapp.ui.theme.BtnGreen
import com.example.mytrackerapp.ui.theme.BtnYellow
import com.example.mytrackerapp.ui.theme.GradientEnd
import com.example.mytrackerapp.ui.theme.GradientStart
import com.example.mytrackerapp.ui.theme.Grey1
import com.example.mytrackerapp.ui.theme.Grey2
import com.example.mytrackerapp.ui.theme.Pink1
import com.example.mytrackerapp.ui.theme.PurpleLight
import com.example.mytrackerapp.ui.theme.poppinsFamily
import kotlin.text.toFloat

// Dashboard Screen - Updated with Set Goal button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    sessionManager: SessionManager,
    navController: NavHostController
) {
    val username = sessionManager.getUsername()
    val context = androidx.compose.ui.platform.LocalContext.current
    val apiHelper = remember { APIHelper(context) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var progressData by remember { mutableStateOf<ProgressData?>(null) }
    var isLoadingProgress by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Load progress data
    LaunchedEffect(Unit) {
        val userId = sessionManager.getUserId()
        apiHelper.getProfile(
            userId = userId,
            onSuccess = { profile ->
                if (profile != null) {
                    weight = profile.weight.toString()
                    height = profile.height.toString()
                }
                isLoadingProgress = false
            },
            onError = { error ->
                errorMessage = error
                isLoadingProgress = false
            }
        )
        apiHelper.getProgress(
            userId = userId,
            onSuccess = { data ->
                progressData = data
                isLoadingProgress = false
            },
            onError = {
                isLoadingProgress = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text="FIT TRAC APP", fontSize = 20.sp, fontWeight = FontWeight.Bold )},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                ),
                actions = {
                    IconButton(
                        onClick = { navController.navigate("profile") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                    }
                    IconButton(
                        onClick = {
                            sessionManager.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = BtnYellow
                        )
                    }
                }
            )
        },

        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { navController.navigate("dashboard") }) {
                            Icon(
                                Icons.Default.HouseSiding,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = BtnYellow
                            )
                        }
                        Text("Home", fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { navController.navigate("set_goal") }) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = BtnGreen
                            )
                        }
                        Text("Set Goals", fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { navController.navigate("activity") }) {
                            Icon(
                                Icons.Default.DirectionsRun,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = BtnGreen
                            )
                        }
                        Text("Activities", fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { navController.navigate("all_activities") }) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BtnGreen
                            )
                        }
                        Text("Progress", fontSize = 12.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "Welcome Back,",
                        color = Grey1,
                        fontSize = 12.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        "$username!",
                        color = Black,
                        fontSize = 20.sp,
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.Bold
                    )

                }
                Box() {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img1),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentScale = ContentScale.FillWidth

                        )
                        Column(
                            modifier = Modifier
                                .height(200.dp)
                                .padding(start = 18.dp, top = 50.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "BMI (Body Mass Index)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = poppinsFamily,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (bmi != null) {
                                Text(
                                    text = String.format("%.1f", bmi),
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "You are: $bmiCategory",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    "Your BMI: -",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "You are: -",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = poppinsFamily,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Button(

                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(start = 0.dp, end = 0.dp, bottom = 0.dp)
                                    .background(
                                        color = BtnYellow,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .height(30.dp),
                                onClick = { navController.navigate("profile")},
                                contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                            ) {
                                Text(
                                    "View Profile", color = Color.White, fontSize = 14.sp,
                                    fontFamily = poppinsFamily, fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = PurpleLight, shape = RoundedCornerShape(12.dp))
                    .padding(15.dp)
            ) {

                Text(
                    "Select Your Goal", color = Black, fontSize = 14.sp,
                    fontFamily = poppinsFamily, fontWeight = FontWeight.SemiBold
                )
                Button(
                    modifier = Modifier
                        .padding(start = 0.dp, end = 0.dp, bottom = 0.dp)
                        .background(
                            color = BtnGreen,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .height(30.dp),
                    onClick = { navController.navigate("set_goal") },
                    contentPadding = PaddingValues(start = 30.dp, end = 30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        "Check", color = Color.White, fontSize = 14.sp,
                        fontFamily = poppinsFamily, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Stats Cards Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Progress",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = if (progressData?.hasGoals == true) "Active Goals" else "No Goals Set",
                        fontSize = 14.sp,
                        color = if (progressData?.hasGoals == true) Color(0xFFB0C929) else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (isLoadingProgress) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Red)
                    }
                } else if (progressData?.hasGoals == true) {
                    // Redesigned Progress Cards
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        progressData?.walking?.let { data ->
                            item {
                                ActivityProgressCard(
                                    activityType = "Walking",
                                    percentage = data.percentage.toFloat(),
                                    calories = data.calories,
                                    icon = Icons.Default.DirectionsWalk,
                                    gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A))
                                )
                            }
                        }

                        progressData?.running?.let { data ->
                            item {
                                ActivityProgressCard(
                                    activityType = "Running",
                                    percentage = data.percentage.toFloat(),
                                    calories = data.calories,
                                    icon = Icons.Default.DirectionsRun,
                                    gradientColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
                                )
                            }
                        }

                        progressData?.swimming?.let { data ->
                            item {
                                ActivityProgressCard(
                                    activityType = "Swimming",
                                    percentage = data.percentage.toFloat(),
                                    calories = data.calories,
                                    icon = Icons.Default.Pool,
                                    gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
                                )
                            }
                        }
                    }
                } else {
                    // No goals set message
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Yellow
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Active Goals",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Set your first goal to start tracking progress",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))


                // Track Activity Button - NEW
                Button(
                    onClick = { navController.navigate("activity") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BtnGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        Icons.Default.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Track Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Additional action cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("view_goals") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Goals")
                    }

                    OutlinedButton(
                        onClick = { navController.navigate("all_activities") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activities")
                    }
                }
            }
        }
    }
}

    @Composable
    fun ActivityProgressCard(
        activityType: String,
        percentage: Float,
        calories: Int,
        icon: ImageVector,
        gradientColors: List<Color>
    ) {
        Card(
            modifier = Modifier.width(160.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress Circle with Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    // Background Circle
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawArc(
                            color = gradientColors[0].copy(alpha = 0.1f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 8.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )

                        // Progress Arc
                        drawArc(
                            color = gradientColors[0],
                            startAngle = -90f,
                            sweepAngle = (percentage / 100f) * 360f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 8.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                    }

                    // Icon in Center
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = gradientColors,
                                    center = Offset(0.5f, 0.5f),
                                    radius = 0.7f
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Activity Info
                Text(
                    text = activityType,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$calories kcal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Percentage with progress indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp),
                        color = gradientColors[0],
                        trackColor = gradientColors[0].copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${percentage.toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradientColors[0]
                    )
                }
            }
        }
    }

@Composable
fun WeeklyLineChart(weeklyStats: List<DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem("Walking", Color(0xFF4CAF50))
                LegendItem("Running", Color(0xFFFF9800))
                LegendItem("Swimming", Color(0xFF2196F3))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Area
            val maxValue = weeklyStats.maxOfOrNull { maxOf(it.walking, it.running, it.swimming) } ?: 1
            val chartHeight = 200.dp

            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
            ) {
                val width = size.width
                val height = size.height
                val spacing = width / (weeklyStats.size + 1)

                // Draw grid lines
                for (i in 0..5) {
                    val y = height * (i / 5f)
                    drawLine(
                        color = androidx.compose.ui.graphics.Color.LightGray,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Draw lines for each activity type
                if (maxValue > 0) {
                    // Walking line
                    drawActivityLine(
                        weeklyStats,
                        spacing,
                        height,
                        maxValue,
                        { it.walking },
                        androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    )

                    // Running line
                    drawActivityLine(
                        weeklyStats,
                        spacing,
                        height,
                        maxValue,
                        { it.running },
                        androidx.compose.ui.graphics.Color(0xFFFF9800)
                    )

                    // Swimming line
                    drawActivityLine(
                        weeklyStats,
                        spacing,
                        height,
                        maxValue,
                        { it.swimming },
                        androidx.compose.ui.graphics.Color(0xFF2196F3)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-axis labels (day names)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyStats.forEach { stat ->
                    Text(
                        text = stat.dayShort,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = MaterialTheme.shapes.small)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawActivityLine(
    stats: List<DailyStats>,
    spacing: Float,
    height: Float,
    maxValue: Int,
    getValue: (DailyStats) -> Int,
    color: androidx.compose.ui.graphics.Color
) {
    val points = mutableListOf<androidx.compose.ui.geometry.Offset>()

    stats.forEachIndexed { index, stat ->
        val value = getValue(stat)
        val x = spacing * (index + 1)
        val y = height - (height * (value.toFloat() / maxValue))
        points.add(androidx.compose.ui.geometry.Offset(x, y))
    }

    // Draw lines connecting points
    for (i in 0 until points.size - 1) {
        drawLine(
            color = color,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 4f,
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }

    // Draw points
    points.forEach { point ->
        drawCircle(
            color = color,
            radius = 6f,
            center = point
        )
    }
}

@Composable
fun WeeklyCaloriesBarChart(weeklyCalories: List<DailyCalories>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Total calories for the week
            val totalCalories = weeklyCalories.sumOf { it.calories }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: $totalCalories kcal",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Calories",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart
            val maxCalories = weeklyCalories.maxOfOrNull { it.calories } ?: 1
            val chartHeight = 180.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyCalories.forEach { day ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Calorie value on top
                        if (day.calories > 0) {
                            Text(
                                text = "${day.calories}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Bar
                        val barHeight = if (maxCalories > 0) {
                            (chartHeight - 30.dp) * (day.calories.toFloat() / maxCalories)
                        } else {
                            0.dp
                        }

                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(maxOf(barHeight, if (day.calories > 0) 10.dp else 0.dp))
                                .background(
                                    color = Color(0xFFF44336),
                                    shape = MaterialTheme.shapes.small
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Day label
                        Text(
                            text = day.dayShort,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}