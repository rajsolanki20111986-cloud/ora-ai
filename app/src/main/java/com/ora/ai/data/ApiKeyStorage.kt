package com.ora.ai.data

import android.content.Context

class ApiKeyStorage(
    context: Context
) {

    private val preferences = context.getSharedPreferences(
        "ora_settings",
        Context.MODE_PRIVATE
    )

    fun saveApiKey(apiKey: String) {
        preferences.edit()
            .putString(KEY_API_KEY, apiKey.trim())
            .apply()
    }

    fun getApiKey(): String {
        return preferences.getString(KEY_API_KEY, "") ?: ""
    }

    fun clearApiKey() {
        preferences.edit()
            .remove(KEY_API_KEY)
            .apply()
    }

    companion object {
        private const val KEY_API_KEY = "gemini_api_key"
    }
}
