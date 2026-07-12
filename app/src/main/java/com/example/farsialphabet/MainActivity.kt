package com.example.farsialphabet

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.farsialphabet.ui.PracticeScreen
import com.example.farsialphabet.ui.SettingsScreen

enum class Screen {
    Practice, Settings
}

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Practice) }

                    when (currentScreen) {
                        Screen.Practice -> {
                            PracticeScreen(
                                settingsRepository = settingsRepository,
                                onNavigateToSettings = { currentScreen = Screen.Settings },
                                speakLetter = ::speakLetter
                            )
                        }
                        Screen.Settings -> {
                            SettingsScreen(
                                settingsRepository = settingsRepository,
                                onNavigateBack = { currentScreen = Screen.Practice },
                                speakLetter = ::speakLetter
                            )
                        }
                    }
                }
            }
        }
    }

    private fun speakLetter(letterId: Int) {
        val resId = resources.getIdentifier("letter_$letterId", "raw", packageName)
        if (resId != 0) {
            val mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
            mediaPlayer?.start()
        }
    }
}
