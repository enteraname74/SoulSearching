package com.github.enteraname74.soulsearching.feature.search.all

import androidx.navigation3.runtime.NavKey

interface SearchAllNavScope {
    fun navigateBack()
    fun toDestination(destination: NavKey)
}