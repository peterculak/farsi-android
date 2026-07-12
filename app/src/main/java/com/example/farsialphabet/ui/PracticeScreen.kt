package com.example.farsialphabet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farsialphabet.FarsiLetter
import com.example.farsialphabet.LetterRepository
import com.example.farsialphabet.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    settingsRepository: SettingsRepository,
    onNavigateToSettings: () -> Unit
) {
    val enabledLetters = remember { mutableStateOf(settingsRepository.getEnabledLetters()) }
    val isReverseMode = remember { mutableStateOf(settingsRepository.isReverseModeEnabled) }
    val isAutoTransition = remember { mutableStateOf(settingsRepository.isAutoTransitionEnabled) }
    val autoTransitionDelay = remember { mutableStateOf(settingsRepository.autoTransitionDelayMs) }

    LaunchedEffect(Unit) {
        enabledLetters.value = settingsRepository.getEnabledLetters()
        isReverseMode.value = settingsRepository.isReverseModeEnabled
        isAutoTransition.value = settingsRepository.isAutoTransitionEnabled
        autoTransitionDelay.value = settingsRepository.autoTransitionDelayMs
    }

    if (enabledLetters.value.size < 3) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Practice") },
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Please enable at least 3 letters in settings.", fontSize = 18.sp)
            }
        }
        return
    }

    var targetLetter by remember { mutableStateOf(enabledLetters.value.random()) }
    var options by remember { mutableStateOf(generateOptions(targetLetter, enabledLetters.value, isReverseMode.value)) }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Black) }
    var answered by remember { mutableStateOf(false) }
    var selectedOptionId by remember { mutableStateOf<Int?>(null) }

    fun nextQuestion() {
        var nextLetter = enabledLetters.value.random()
        if (enabledLetters.value.size > 1) {
            while (nextLetter.id == targetLetter.id) {
                nextLetter = enabledLetters.value.random()
            }
        }
        targetLetter = nextLetter
        options = generateOptions(targetLetter, enabledLetters.value, isReverseMode.value)
        feedbackMessage = ""
        answered = false
        selectedOptionId = null
    }

    LaunchedEffect(answered) {
        if (answered && isAutoTransition.value) {
            kotlinx.coroutines.delay(autoTransitionDelay.value)
            nextQuestion()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reverse", fontSize = 14.sp)
                        Switch(
                            checked = isReverseMode.value,
                            onCheckedChange = {
                                isReverseMode.value = it
                                settingsRepository.isReverseModeEnabled = it
                                nextQuestion()
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isReverseMode.value) {
                Text(
                    text = "What is the Farsi letter for:", 
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = targetLetter.transliteration, fontSize = 64.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    text = "What is the English transliteration for:", 
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "${targetLetter.fullForm}   ${targetLetter.shortForm}", fontSize = 64.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            options.forEach { option ->
                Button(
                    onClick = {
                        if (!answered) {
                            answered = true
                            selectedOptionId = option.id
                            if (option.id == targetLetter.id) {
                                feedbackMessage = "Correct!"
                                feedbackColor = Color(0xFF4CAF50) // Green
                            } else {
                                feedbackMessage = "Incorrect!"
                                feedbackColor = Color.Red
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (answered) {
                            if (option.id == targetLetter.id) Color(0xFF4CAF50)
                            else if (option.id == selectedOptionId) Color.Red
                            else MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f).padding(8.dp)
                ) {
                    if (isReverseMode.value) {
                        Text(text = "${option.fullForm}   ${option.shortForm}", fontSize = 24.sp)
                    } else {
                        Text(text = option.transliteration, fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (feedbackMessage.isNotEmpty()) feedbackMessage else " ", 
                color = feedbackColor, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { nextQuestion() },
                modifier = Modifier.padding(16.dp).alpha(if (answered) 1f else 0f),
                enabled = answered
            ) {
                Text("Next Question", fontSize = 20.sp)
            }
        }
    }
}

fun generateOptions(target: FarsiLetter, pool: List<FarsiLetter>, isReverse: Boolean): List<FarsiLetter> {
    val options = mutableListOf(target)
    val poolShuffled = pool.shuffled()
    
    for (letter in poolShuffled) {
        if (letter.id != target.id && options.size < 3) {
            if (!isReverse) {
                if (options.none { it.transliteration == letter.transliteration }) {
                    options.add(letter)
                }
            } else {
                if (options.none { it.id == letter.id }) {
                    options.add(letter)
                }
            }
        }
    }
    
    val allLettersShuffled = LetterRepository.letters.shuffled()
    for (letter in allLettersShuffled) {
        if (options.size >= 3) break
        if (letter.id != target.id) {
            if (!isReverse) {
                if (options.none { it.transliteration == letter.transliteration }) {
                    options.add(letter)
                }
            } else {
                if (options.none { it.id == letter.id }) {
                    options.add(letter)
                }
            }
        }
    }
    
    return options.shuffled()
}
