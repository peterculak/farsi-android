package com.example.farsialphabet.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.farsialphabet.LetterRepository

import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LearningScreen(speakLetter: (Int) -> Unit, resetTrigger: Int) {
    val letters = LetterRepository.letters
    val pagerState = rememberPagerState(pageCount = { letters.size })

    LaunchedEffect(resetTrigger) {
        if (resetTrigger > 0) {
            pagerState.animateScrollToPage(0)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val letter = letters[page]
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${letter.fullForm}   ${letter.shortForm}",
                fontSize = 100.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "${letter.name} (${letter.transliteration})",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            IconButton(
                onClick = { speakLetter(letter.id) },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Pronounce",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
