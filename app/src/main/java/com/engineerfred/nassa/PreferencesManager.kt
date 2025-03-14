package com.engineerfred.nassa

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    context: Context
) {

    companion object {
        private val TAG = PreferencesManager::class.java.name
    }
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean("isFirstLaunch", true)
    }

    fun setFirstLaunchFalse() {
        prefs.edit { putBoolean("isFirstLaunch", false) }
    }

    fun getTheme() : ThemeOption {
        return when (prefs.getString("theme", "light")) {
            "light" -> ThemeOption.Light
            "dark" -> ThemeOption.Dark
            else -> ThemeOption.System
        }
    }

    fun saveTheme(theme: ThemeOption) {
        try {
            prefs.edit { putString("theme", theme.name.lowercase()) }
            Log.i(TAG, "Theme Saved!")
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
        }
    }
}