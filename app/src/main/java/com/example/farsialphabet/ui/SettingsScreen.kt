package com.example.farsialphabet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.farsialphabet.LetterRepository
import com.example.farsialphabet.SettingsRepository
import com.example.farsialphabet.AudioRecorderHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    speakLetter: (Int) -> Unit,
    audioRecorderHelper: AudioRecorderHelper
) {
    var autoDelay by remember { mutableStateOf(settingsRepository.autoTransitionDelayMs.toFloat()) }
    
    val context = LocalContext.current
    var recordingLetterId by remember { mutableStateOf<Int?>(null) }
    var hasRecordPermission by remember { 
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    var pendingRecordId by remember { mutableStateOf<Int?>(null) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasRecordPermission = isGranted
        if (isGranted && pendingRecordId != null) {
            audioRecorderHelper.startRecording(pendingRecordId!!)
            recordingLetterId = pendingRecordId
            pendingRecordId = null
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        Text(
                            text = "Time before next question: ${String.format("%.1f", autoDelay / 1000f)} seconds", 
                            fontSize = 18.sp, 
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                        Text(
                            text = "Controls how quickly the next flashcard appears after answering.", 
                            fontSize = 14.sp, 
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        Slider(
                            value = autoDelay,
                            onValueChange = { 
                                autoDelay = it
                                settingsRepository.autoTransitionDelayMs = it.toLong()
                            },
                            valueRange = 250f..5000f,
                            steps = 18,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp)
                        )
                    }

                    HorizontalDivider()
                }

                items(LetterRepository.letters) { letter ->
                    var isEnabled by remember { mutableStateOf(settingsRepository.isLetterEnabled(letter.id)) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "${letter.name} (${letter.transliteration})", fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "${letter.fullForm}  ${letter.shortForm}", fontSize = 24.sp)
                                IconButton(onClick = { speakLetter(letter.id) }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Pronounce", modifier = Modifier.size(24.dp))
                                }
                                val isThisRecording = recordingLetterId == letter.id
                                var hasCustom by remember { mutableStateOf(audioRecorderHelper.hasCustomRecording(letter.id)) }

                                Button(
                                    onClick = {
                                        if (isThisRecording) {
                                            audioRecorderHelper.stopRecording()
                                            recordingLetterId = null
                                            hasCustom = true
                                        } else {
                                            if (recordingLetterId != null) {
                                                audioRecorderHelper.stopRecording()
                                            }
                                            if (hasRecordPermission) {
                                                audioRecorderHelper.startRecording(letter.id)
                                                recordingLetterId = letter.id
                                            } else {
                                                pendingRecordId = letter.id
                                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isThisRecording) Color.Red else MaterialTheme.colorScheme.secondary
                                    ),
                                    modifier = Modifier.padding(start = 8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text(if (isThisRecording) "Stop" else "Rec", fontSize = 12.sp)
                                }

                                if (hasCustom && !isThisRecording) {
                                    Button(
                                        onClick = {
                                            audioRecorderHelper.deleteRecording(letter.id)
                                            hasCustom = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                        modifier = Modifier.padding(start = 4.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Restore", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { 
                                isEnabled = it
                                settingsRepository.setLetterEnabled(letter.id, it)
                            }
                        )
                    }
                    HorizontalDivider()
                }
        }
    }
}
