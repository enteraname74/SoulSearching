package com.github.soulsearching.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.ColorPaletteUtils
import com.github.soulsearching.classes.PlayerUtils

object DynamicColor {
    val primary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.primary
            } else {
                ColorPaletteUtils.getPrimaryColor()
            },
            tween(Constants.AnimationTime.normal)
        ).value

    val onPrimary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                Color.White
            },
            tween(Constants.AnimationTime.normal)
        ).value

    val outline: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                Color.LightGray
            },
            tween(Constants.AnimationTime.normal)
        ).value


    val secondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.secondary
            } else {
                ColorPaletteUtils.getSecondaryColor()
            },
            tween(Constants.AnimationTime.normal)
        ).value

    val onSecondary: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                Color.White
            },
            tween(Constants.AnimationTime.normal)
        ).value

    val subText: Color
        @Composable
        get() = animateColorAsState(
            targetValue =
            if (PlayerUtils.playerViewModel.currentColorPalette == null) {
                MaterialTheme.colorScheme.outline
            } else {
                Color.LightGray
            },
            tween(Constants.AnimationTime.normal)
        ).value
}