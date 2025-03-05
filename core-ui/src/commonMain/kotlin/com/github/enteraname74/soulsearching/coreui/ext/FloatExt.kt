package com.github.enteraname74.soulsearching.coreui.ext

fun Float.coerceForProgressBar(): Float =
    this.coerceIn(
        minimumValue = 0f,
        maximumValue = 1f,
    )