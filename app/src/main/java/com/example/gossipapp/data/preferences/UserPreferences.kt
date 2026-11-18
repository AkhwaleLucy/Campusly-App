package com.example.gossipapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "campusly_user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val IS_ONBOARDED = booleanPreferencesKey("is_onboarded")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val CURRENT_USER_EMAIL = stringPreferencesKey("current_user_email")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SELECTED_UNIVERSITY = stringPreferencesKey("selected_university")
        val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        val QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val IS_ANONYMOUS_MODE = booleanPreferencesKey("is_anonymous_mode")
    }

    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ONBOARDED] ?: false
    }

    suspend fun setOnboarded(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_ONBOARDED] = completed
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = loggedIn
        }
    }

    val currentUserEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENT_USER_EMAIL] ?: "student@campusly.app"
    }

    suspend fun setCurrentUserEmail(email: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_USER_EMAIL] = email
        }
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    val selectedUniversity: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_UNIVERSITY] ?: "University of Science & Technology"
    }

    suspend fun setSelectedUniversity(university: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_UNIVERSITY] = university
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val quietHoursEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[QUIET_HOURS_ENABLED] ?: false
    }

    suspend fun setQuietHoursEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[QUIET_HOURS_ENABLED] = enabled
        }
    }

    val quietHoursStart: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[QUIET_HOURS_START] ?: "22:00"
    }

    suspend fun setQuietHoursStart(time: String) {
        context.dataStore.edit { prefs ->
            prefs[QUIET_HOURS_START] = time
        }
    }

    val quietHoursEnd: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[QUIET_HOURS_END] ?: "07:00"
    }

    suspend fun setQuietHoursEnd(time: String) {
        context.dataStore.edit { prefs ->
            prefs[QUIET_HOURS_END] = time
        }
    }

    val isAnonymousMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ANONYMOUS_MODE] ?: false
    }

    suspend fun setAnonymousMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_ANONYMOUS_MODE] = enabled
        }
    }
}
