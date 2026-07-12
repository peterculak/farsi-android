package com.example.farsialphabet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onNavigateBack: () -> Unit,
    speakText: (String) -> Unit
) {
    var reverseMode by remember { mutableStateOf(settingsRepository.isReverseModeEnabled) }
    var autoTransition by remember { mutableStateOf(settingsRepository.isAutoTransitionEnabled) }
    var autoDelay by remember { mutableStateOf(settingsRepository.autoTransitionDelayMs.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Reverse Mode (English -> Farsi)", 
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).padding(end = 16.dp)
                )
                Switch(
                    checked = reverseMode,
                    onCheckedChange = { 
                        reverseMode = it
                        settingsRepository.isReverseModeEnabled = it
                    }
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto Transition", 
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f).padding(end = 16.dp)
                )
                Switch(
                    checked = autoTransition,
                    onCheckedChange = { 
                        autoTransition = it
                        settingsRepository.isAutoTransitionEnabled = it
                    }
                )
            }
            
            if (autoTransition) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Delay: ${autoDelay.toLong()} ms", fontSize = 16.sp, modifier = Modifier.width(130.dp))
                    Slider(
                        value = autoDelay,
                        onValueChange = { 
                            autoDelay = it
                            settingsRepository.autoTransitionDelayMs = it.toLong()
                        },
                        valueRange = 250f..5000f,
                        steps = 18,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider()

            LazyColumn(modifier = Modifier.weight(1f)) {
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
                                IconButton(onClick = { speakText(letter.fullForm) }) {
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
}
