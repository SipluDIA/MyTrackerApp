package com.example.mytrackerapp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    fun saveSession(username: String, userId: Int) {
        prefs.edit().apply {
            putString("username", username)
            putInt("user_id", userId)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun getUsername(): String {
        return prefs.getString("username", "") ?: ""
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", 0)
    }
}
