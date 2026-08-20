package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.navigation3.runtime.NavKey

interface MusicListDetailNavScope {
    fun navigateBack()
    fun toDestination(destination: NavKey)
}