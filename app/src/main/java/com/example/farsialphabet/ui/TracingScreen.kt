package com.example.farsialphabet.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farsialphabet.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracingScreen(
    settingsRepository: SettingsRepository,
    speakLetter: (Int) -> Unit,
    isVisible: Boolean
) {
    val enabledLetters = remember { mutableStateOf(settingsRepository.getEnabledLetters()) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            enabledLetters.value = settingsRepository.getEnabledLetters()
        }
    }

    if (enabledLetters.value.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Please enable at least 1 letter in settings.", fontSize = 18.sp)
        }
        return
    }

    var targetLetter by remember { mutableStateOf(enabledLetters.value.random()) }
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var drawTrigger by remember { mutableStateOf(0) }

    fun nextLetter() {
        var next = enabledLetters.value.random()
        if (enabledLetters.value.size > 1) {
            while (next.id == targetLetter.id) {
                next = enabledLetters.value.random()
            }
        }
        targetLetter = next
        paths.clear()
        currentPath = null
        drawTrigger = 0
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = targetLetter.name,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "(${targetLetter.transliteration})",
            fontSize = 24.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            val newPath = Path().apply {
                                moveTo(offset.x, offset.y)
                                lineTo(offset.x + 0.1f, offset.y)
                            }
                            paths.add(newPath)
                            drawTrigger++
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val newPath = Path().apply { moveTo(offset.x, offset.y) }
                            currentPath = newPath
                            paths.add(newPath)
                            drawTrigger++
                        },
                        onDragEnd = {
                            currentPath = null
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        currentPath?.lineTo(change.position.x, change.position.y)
                        drawTrigger++
                    }
                }
        ) {
            // The grey template letter
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "${targetLetter.fullForm}   ${targetLetter.shortForm}",
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
            }

            // The drawing canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawTrigger // Force recomposition
                paths.forEach { path ->
                    drawPath(
                        path = path,
                        color = Color.Black,
                        style = Stroke(
                            width = 15f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Clear button
            Button(
                onClick = { 
                    paths.clear() 
                    drawTrigger++
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
            ) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { speakLetter(targetLetter.id) },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Pronounce", modifier = Modifier.fillMaxSize())
            }

            Button(
                onClick = { nextLetter() },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Next Letter", fontSize = 20.sp)
            }
        }
    }
}
