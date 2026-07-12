package com.example.farsialphabet

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
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

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var settingsRepository: SettingsRepository
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)
        tts = TextToSpeech(this, this)

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
                                speakText = ::speak
                            )
                        }
                        Screen.Settings -> {
                            SettingsScreen(
                                settingsRepository = settingsRepository,
                                onNavigateBack = { currentScreen = Screen.Practice },
                                speakText = ::speak
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = java.util.Locale("fa", "IR")
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported or missing data")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
}
