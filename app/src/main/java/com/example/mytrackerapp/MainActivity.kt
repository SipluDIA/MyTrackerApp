package com.example.mytrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val apiHelper = APIHelper(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val startDestination = if (sessionManager.isLoggedIn()) "dashboard" else "login"

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("register") {
                        RegisterScreen(
                            navController = navController,
                            apiHelper = apiHelper
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            apiHelper = apiHelper,
                            sessionManager = sessionManager
                        )
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            sessionManager = sessionManager,
                            navController = navController
                        )
                    }
                    composable("set_goal") {
                        SetGoalScreen(
                            navController = navController,
                            apiHelper = apiHelper,
                            sessionManager = sessionManager
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            navController = navController,
                            apiHelper = apiHelper,
                            sessionManager = sessionManager
                        )
                    }
                    composable("activity") {
                        ActivityScreen(
                            navController = navController,
                            apiHelper = apiHelper,
                            sessionManager = sessionManager
                        )
                    }
                    composable("all_activities") {
                        AllActivitiesScreen(
                            navController = navController,
                            apiHelper = apiHelper,
                            sessionManager = sessionManager
                        )
                    }
                }
            }
        }
    }
}
