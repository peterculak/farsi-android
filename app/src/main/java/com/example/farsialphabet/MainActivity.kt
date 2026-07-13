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
import com.example.farsialphabet.ui.LearningScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.dp

enum class Screen {
    Learning, Practice, Settings
}

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var audioRecorderHelper: AudioRecorderHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)
        audioRecorderHelper = AudioRecorderHelper(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Learning) }
                    var learnResetTrigger by remember { mutableStateOf(0) }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Learn") },
                                    label = { Text("Learn") },
                                    selected = currentScreen == Screen.Learning,
                                    onClick = {
                                        if (currentScreen == Screen.Learning) {
                                            learnResetTrigger++
                                        } else {
                                            currentScreen = Screen.Learning
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Edit, contentDescription = "Practice") },
                                    label = { Text("Practice") },
                                    selected = currentScreen == Screen.Practice,
                                    onClick = { currentScreen = Screen.Practice }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings") },
                                    selected = currentScreen == Screen.Settings,
                                    onClick = { currentScreen = Screen.Settings }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                            Box(modifier = Modifier.fillMaxSize().offset(x = if (currentScreen == Screen.Learning) 0.dp else 10000.dp)) {
                                LearningScreen(speakLetter = ::speakLetter, resetTrigger = learnResetTrigger)
                            }
                            Box(modifier = Modifier.fillMaxSize().offset(x = if (currentScreen == Screen.Practice) 0.dp else 10000.dp)) {
                                PracticeScreen(
                                    settingsRepository = settingsRepository,
                                    speakLetter = ::speakLetter,
                                    isVisible = currentScreen == Screen.Practice
                                )
                            }
                            Box(modifier = Modifier.fillMaxSize().offset(x = if (currentScreen == Screen.Settings) 0.dp else 10000.dp)) {
                                SettingsScreen(
                                    settingsRepository = settingsRepository,
                                    speakLetter = ::speakLetter,
                                    audioRecorderHelper = audioRecorderHelper
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun speakLetter(letterId: Int) {
        val customFile = audioRecorderHelper.getCustomRecordingFile(letterId)
        
        if (customFile.exists()) {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(customFile.absolutePath)
                prepare()
                setOnCompletionListener { release() }
                start()
            }
        } else {
            val resId = resources.getIdentifier("letter_$letterId", "raw", packageName)
            if (resId != 0) {
                MediaPlayer.create(this, resId)?.apply {
                    setOnCompletionListener { release() }
                    start()
                }
            }
        }
    }
}
