package com.github.cwramirezg.micredito.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "micredito_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_USER_LOGGED_IN = booleanPreferencesKey("user_logged_in")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_USER_TOKEN = stringPreferencesKey("user_token")
        val KEY_CLIENT_ID = stringPreferencesKey("client_id")
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    fun isUserLoggedIn(): Flow<Boolean> = dataStore.data.map { preferences ->
        (preferences[KEY_USER_LOGGED_IN] ?: false) && (preferences[KEY_USER_TOKEN]
            ?: "").isNotEmpty()
    }

    suspend fun setUserLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_LOGGED_IN] = isLoggedIn
        }
    }

    fun hasCompletedOnboarding(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    fun getUserToken(): Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_USER_TOKEN] ?: ""
    }

    suspend fun setUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_TOKEN] = token
        }
    }

    fun getClientId(): Flow<String> = dataStore.data.map { preferences ->
        preferences[KEY_CLIENT_ID] ?: ""
    }

    suspend fun setClientId(clientId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_CLIENT_ID] = clientId
        }
    }

    fun isFirstLaunch(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun markFirstLaunchCompleted() {
        dataStore.edit { preferences ->
            preferences[KEY_FIRST_LAUNCH] = false
        }
    }

    suspend fun checkAndMarkFirstLaunch(): Boolean {
        val isFirst = dataStore.data.first()[KEY_FIRST_LAUNCH] ?: true
        if (isFirst) {
            markFirstLaunchCompleted()
        }
        return isFirst
    }

    suspend fun clearAllData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[KEY_USER_LOGGED_IN] = false
            preferences[KEY_USER_TOKEN] = ""
            preferences[KEY_CLIENT_ID] = ""
        }
    }

    suspend fun saveUserSession(token: String, clientId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_TOKEN] = token
            preferences[KEY_CLIENT_ID] = clientId
            preferences[KEY_USER_LOGGED_IN] = true
        }
    }
}
