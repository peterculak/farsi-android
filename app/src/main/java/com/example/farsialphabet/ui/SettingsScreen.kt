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
import com.example.farsialphabet.LetterRepository
import com.example.farsialphabet.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    speakLetter: (Int) -> Unit
) {
    var autoDelay by remember { mutableStateOf(settingsRepository.autoTransitionDelayMs.toFloat()) }

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
