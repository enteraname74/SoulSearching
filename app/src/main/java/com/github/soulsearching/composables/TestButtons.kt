package com.github.soulsearching.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.soulsearching.events.MusicEvent

@Composable
fun TestButtons(
    addMusic : () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        item {
            Button(onClick = {
                addMusic()
            }) {
                Text("Test ajout simple")
            }
        }
    }
}