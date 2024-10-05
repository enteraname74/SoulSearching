package com.github.enteraname74.soulsearching.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import org.jaudiotagger.audio.AudioFileIO
import java.io.File

@Composable
actual fun CoverPathImage(
    initialCoverPath: String?,
    modifier: Modifier,
    tint: Color,
    contentScale: ContentScale,
) {

    var fileData: ByteArray? by remember { mutableStateOf(null) }

    LaunchedEffect(initialCoverPath) {
        if (initialCoverPath == null) {
            fileData = null
            return@LaunchedEffect
        }
        try {
            val audioFile = AudioFileIO.read(File(initialCoverPath))
            fileData = audioFile.tag.firstArtwork.binaryData
        } catch (_: Exception) {
            fileData = null
        }
    }

    if (initialCoverPath != null) {
        DataImage(
            data = fileData,
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        TemplateImage(
            modifier = modifier,
            contentScale = contentScale,
            tint = tint,
        )
    }
}