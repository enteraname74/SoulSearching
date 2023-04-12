package com.github.soulsearching.composables

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.github.soulsearching.R

@SuppressLint("UnnecessaryComposedModifier")
@Composable
fun AppImage(
    bitmap : Bitmap?,
    size : Dp,
    modifier: Modifier = Modifier
) {
    val modifierBase = Modifier
        .size(size)
        .clip(RoundedCornerShape(percent = 10))
        .composed {
            modifier
        }

    if (bitmap != null) {
        Image(
            modifier = modifierBase,
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(id = R.string.image),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            modifier = modifierBase,
            painter = painterResource(id = R.drawable.ic_saxophone_svg),
            contentDescription = stringResource(id = R.string.image),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary)
        )
    }

}