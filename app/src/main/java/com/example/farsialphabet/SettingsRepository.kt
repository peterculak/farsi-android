package com.example.farsialphabet

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("farsi_settings", Context.MODE_PRIVATE)

    var isReverseModeEnabled: Boolean
        get() = prefs.getBoolean("reverse_mode", false)
        set(value) = prefs.edit().putBoolean("reverse_mode", value).apply()

    var isHardModeEnabled: Boolean
        get() = prefs.getBoolean("hard_mode", false)
        set(value) = prefs.edit().putBoolean("hard_mode", value).apply()

    var autoTransitionDelayMs: Long
        get() = prefs.getLong("auto_transition_delay", 1000L)
        set(value) = prefs.edit().putLong("auto_transition_delay", value).apply()

    fun isLetterEnabled(letterId: Int): Boolean {
        return prefs.getBoolean("letter_$letterId", true) // Default to true
    }

    fun setLetterEnabled(letterId: Int, enabled: Boolean) {
        prefs.edit().putBoolean("letter_$letterId", enabled).apply()
    }

    fun getEnabledLetters(): List<FarsiLetter> {
        return LetterRepository.letters.filter { isLetterEnabled(it.id) }
    }
}
