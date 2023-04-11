package com.github.soulsearching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.soulsearching.composables.screens.PlaylistScreen
import com.github.soulsearching.ui.theme.SoulSearchingTheme

class SelectedPlaylistActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoulSearchingTheme {
                PlaylistScreen()
            }
        }
    }
}