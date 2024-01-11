package com.github.soulsearching.di

import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
actual inline fun <reified SoulSearchingViewModel>injectViewModel(): SoulSearchingViewModel {
    return koinViewModel()
}