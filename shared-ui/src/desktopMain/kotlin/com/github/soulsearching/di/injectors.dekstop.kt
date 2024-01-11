package com.github.soulsearching.di

import androidx.compose.runtime.Composable

@Composable
actual inline fun <reified SoulSearchingViewModel>injectViewModel(): SoulSearchingViewModel {
    return injectElement()
}