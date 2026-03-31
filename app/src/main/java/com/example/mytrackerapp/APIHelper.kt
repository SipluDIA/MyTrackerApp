package com.example.mytrackerapp

        import android.content.Context
        import com.android.volley.Request
        import com.android.volley.RequestQueue
        import com.android.volley.toolbox.StringRequest
        import com.android.volley.toolbox.Volley
        import org.json.JSONObject

        class APIHelper(private val context: Context) {
            private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

            companion object {
                // IMPORTANT: Replace with your computer's IP address
                private const val BASE_URL = "http://10.0.2.2/auth_api/"
                private const val REGISTER_URL = "${BASE_URL}register.php"
                private const val LOGIN_URL = "${BASE_URL}login.php"
                private const val SET_GOAL_URL = "${BASE_URL}setgoal.php"
                private const val VIEW_GOALS_URL = "${BASE_URL}viewgoals.php"
                private const val PROFILE_URL = "${BASE_URL}profile.php"
                private const val SAVE_ACTIVITY_URL = "${BASE_URL}save_activity.php"
                private const val VIEW_ACTIVITIES_URL = "${BASE_URL}view_activities.php"
                private const val GET_PROGRESS_URL = "${BASE_URL}get_progress.php"
                private const val GET_WEEKLY_STATS_URL = "${BASE_URL}get_weekly_stats.php"
                private const val GET_WEEKLY_CALORIES_URL = "${BASE_URL}get_weekly_calories.php"
            }

            // Register Function
            fun register(
                username: String,
                email: String,
                password: String,
                onSuccess: (String) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    REGISTER_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                onSuccess(jsonResponse.getString("message"))
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "username" to username,
                            "email" to email,
                            "password" to password
                        )
                    }
                }
                requestQueue.add(request)
            }

            // Login Function
            fun login(
                email: String,
                password: String,
                onSuccess: (String, Int) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    LOGIN_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val username = jsonResponse.getString("username")
                                val userId = jsonResponse.getInt("user_id")
                                onSuccess(username, userId)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "email" to email,
                            "password" to password
                        )
                    }
                }
                requestQueue.add(request)
            }

            // Set Goal Function
            fun setGoal(
                userId: Int,
                walking: Int,
                running: Int,
                swimming: Int,
                onSuccess: (String) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    SET_GOAL_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                onSuccess(jsonResponse.getString("message"))
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "user_id" to userId.toString(),
                            "walking" to walking.toString(),
                            "running" to running.toString(),
                            "swimming" to swimming.toString()
                        )
                    }
                }
                requestQueue.add(request)
            }

            // View Goals Function
            fun viewGoals(
                userId: Int,
                onSuccess: (List<Goal>) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    VIEW_GOALS_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val goalsArray = jsonResponse.getJSONArray("goals")
                                val goalsList = mutableListOf<Goal>()

                                for (i in 0 until goalsArray.length()) {
                                    val goalJson = goalsArray.getJSONObject(i)
                                    val goal = Goal(
                                        id = goalJson.getInt("id"),
                                        walking = goalJson.getInt("walking"),
                                        running = goalJson.getInt("running"),
                                        swimming = goalJson.getInt("swimming"),
                                        month = goalJson.getString("month"),
                                        monthName = goalJson.getString("month_name")
                                    )
                                    goalsList.add(goal)
                                }
                                onSuccess(goalsList)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf("user_id" to userId.toString())
                    }
                }
                requestQueue.add(request)
            }

            // Get Profile Function
            fun getProfile(
                userId: Int,
                onSuccess: (Profile?) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    PROFILE_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val exists = jsonResponse.getBoolean("exists")
                                if (exists) {
                                    val profileJson = jsonResponse.getJSONObject("profile")
                                    val profile = Profile(
                                        age = profileJson.getInt("age"),
                                        gender = profileJson.getString("gender"),
                                        weight = profileJson.getDouble("weight"),
                                        height = profileJson.getDouble("height")
                                    )
                                    onSuccess(profile)
                                } else {
                                    onSuccess(null)
                                }
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "user_id" to userId.toString(),
                            "action" to "get"
                        )
                    }
                }
                requestQueue.add(request)
            }

            // Save Profile Function
            fun saveProfile(
                userId: Int,
                age: Int,
                gender: String,
                weight: Double,
                height: Double,
                onSuccess: (String) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    PROFILE_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                onSuccess(jsonResponse.getString("message"))
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "user_id" to userId.toString(),
                            "age" to age.toString(),
                            "gender" to gender,
                            "weight" to weight.toString(),
                            "height" to height.toString(),
                            "action" to "save"
                        )
                    }
                }
                requestQueue.add(request)
            }

            // Save Activity Function
            fun saveActivity(
                userId: Int,
                activityType: String,
                startTime: String,
                endTime: String,
                duration: Int,
                startLatitude: Double,
                startLongitude: Double,
                endLatitude: Double,
                endLongitude: Double,
                distance: Double,
                onSuccess: (String, Int) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    SAVE_ACTIVITY_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val activityId = jsonResponse.getInt("activity_id")
                                onSuccess(jsonResponse.getString("message"), activityId)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "user_id" to userId.toString(),
                            "activity_type" to activityType,
                            "start_time" to startTime,
                            "end_time" to endTime,
                            "duration" to duration.toString(),
                            "start_latitude" to startLatitude.toString(),
                            "start_longitude" to startLongitude.toString(),
                            "end_latitude" to endLatitude.toString(),
                            "end_longitude" to endLongitude.toString(),
                            "distance" to distance.toString()
                        )
                    }
                }
                requestQueue.add(request)
            }

            // View Activities Function
            fun viewActivities(
                userId: Int,
                onSuccess: (List<ActivityData>) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    VIEW_ACTIVITIES_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val activitiesArray = jsonResponse.getJSONArray("activities")
                                val activitiesList = mutableListOf<ActivityData>()

                                for (i in 0 until activitiesArray.length()) {
                                    val activityJson = activitiesArray.getJSONObject(i)
                                    val activity = ActivityData(
                                        id = activityJson.getInt("id"),
                                        activityType = activityJson.getString("activity_type"),
                                        startTime = activityJson.getString("start_time"),
                                        endTime = activityJson.getString("end_time"),
                                        duration = activityJson.getInt("duration"),
                                        distance = activityJson.getDouble("distance"),
                                        formattedDate = activityJson.getString("formatted_date")
                                    )
                                    activitiesList.add(activity)
                                }
                                onSuccess(activitiesList)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf("user_id" to userId.toString())
                    }
                }
                requestQueue.add(request)
            }

            // Get Progress Function
            fun getProgress(
                userId: Int,
                onSuccess: (ProgressData) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    GET_PROGRESS_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val hasGoals = jsonResponse.getBoolean("has_goals")

                                if (hasGoals) {
                                    val progressJson = jsonResponse.getJSONObject("progress")

                                    val walkingJson = progressJson.getJSONObject("walking")
                                    val runningJson = progressJson.getJSONObject("running")
                                    val swimmingJson = progressJson.getJSONObject("swimming")

                                    val progressData = ProgressData(
                                        hasGoals = true,
                                        walking = ActivityProgress(
                                            goal = walkingJson.getInt("goal"),
                                            achieved = walkingJson.getDouble("achieved"),
                                            percentage = walkingJson.getDouble("percentage"),
                                            calories = walkingJson.getInt("calories")
                                        ),
                                        running = ActivityProgress(
                                            goal = runningJson.getInt("goal"),
                                            achieved = runningJson.getDouble("achieved"),
                                            percentage = runningJson.getDouble("percentage"),
                                            calories = runningJson.getInt("calories")
                                        ),
                                        swimming = ActivityProgress(
                                            goal = swimmingJson.getInt("goal"),
                                            achieved = swimmingJson.getDouble("achieved"),
                                            percentage = swimmingJson.getDouble("percentage"),
                                            calories = swimmingJson.getInt("calories")
                                        )
                                    )
                                    onSuccess(progressData)
                                } else {
                                    onSuccess(ProgressData(hasGoals = false))
                                }
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf("user_id" to userId.toString())
                    }
                }
                requestQueue.add(request)
            }

            // Get Weekly Stats Function
            fun getWeeklyStats(
                userId: Int,
                onSuccess: (List<DailyStats>) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    GET_WEEKLY_STATS_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val statsArray = jsonResponse.getJSONArray("weekly_stats")
                                val statsList = mutableListOf<DailyStats>()

                                for (i in 0 until statsArray.length()) {
                                    val statsJson = statsArray.getJSONObject(i)
                                    val stats = DailyStats(
                                        date = statsJson.getString("date"),
                                        dayName = statsJson.getString("day_name"),
                                        dayShort = statsJson.getString("day_short"),
                                        walking = statsJson.getInt("walking"),
                                        running = statsJson.getInt("running"),
                                        swimming = statsJson.getInt("swimming"),
                                        total = statsJson.getInt("total")
                                    )
                                    statsList.add(stats)
                                }
                                onSuccess(statsList)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf("user_id" to userId.toString())
                    }
                }
                requestQueue.add(request)
            }

            // Get Weekly Calories Function
            fun getWeeklyCalories(
                userId: Int,
                onSuccess: (List<DailyCalories>) -> Unit,
                onError: (String) -> Unit
            ) {
                val request = object : StringRequest(
                    Request.Method.POST,
                    GET_WEEKLY_CALORIES_URL,
                    { response ->
                        try {
                            val jsonResponse = JSONObject(response)
                            if (jsonResponse.getBoolean("success")) {
                                val caloriesArray = jsonResponse.getJSONArray("weekly_calories")
                                val caloriesList = mutableListOf<DailyCalories>()

                                for (i in 0 until caloriesArray.length()) {
                                    val caloriesJson = caloriesArray.getJSONObject(i)
                                    val dailyCalories = DailyCalories(
                                        date = caloriesJson.getString("date"),
                                        dayName = caloriesJson.getString("day_name"),
                                        dayShort = caloriesJson.getString("day_short"),
                                        calories = caloriesJson.getInt("calories")
                                    )
                                    caloriesList.add(dailyCalories)
                                }
                                onSuccess(caloriesList)
                            } else {
                                onError(jsonResponse.getString("message"))
                            }
                        } catch (e: Exception) {
                            onError("Error parsing response: ${e.message}")
                        }
                    },
                    { error ->
                        onError(error.message ?: "Network error")
                    }
                ) {
                    override fun getParams(): Map<String, String> {
                        return hashMapOf("user_id" to userId.toString())
                    }
                }
                requestQueue.add(request)
            }
        }

        // Data class for Goal
        data class Goal(
            val id: Int,
            val walking: Int,
            val running: Int,
            val swimming: Int,
            val month: String,
            val monthName: String
        )

        // Data class for Profile
        data class Profile(
            val age: Int,
            val gender: String,
            val weight: Double,
            val height: Double
        )

        // Data class for Activity
        data class ActivityData(
            val id: Int,
            val activityType: String,
            val startTime: String,
            val endTime: String,
            val duration: Int,
            val distance: Double,
            val formattedDate: String
        )

        // Data class for Activity Progress
        data class ActivityProgress(
            val goal: Int = 0,
            val achieved: Double = 0.0,
            val percentage: Double = 0.0,
            val calories: Int = 0
        )

        // Data class for Progress Data
        data class ProgressData(
            val hasGoals: Boolean = false,
            val walking: ActivityProgress = ActivityProgress(),
            val running: ActivityProgress = ActivityProgress(),
            val swimming: ActivityProgress = ActivityProgress()
        )

        // Data class for Daily Stats
        data class DailyStats(
            val date: String,
            val dayName: String,
            val dayShort: String,
            val walking: Int,
            val running: Int,
            val swimming: Int,
            val total: Int
        )

        // Data class for Daily Calories
        data class DailyCalories(
            val date: String,
            val dayName: String,
            val dayShort: String,
            val calories: Int
        )