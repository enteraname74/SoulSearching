package com.github.soulsearching.di

import androidx.compose.runtime.Composable
import org.koin.compose.koinInject

/**
 * Inject a SoulSearchingViewModel.
 */
@Composable
expect inline fun <reified SoulSearchingViewModel>injectViewModel(): SoulSearchingViewModel

/**
 * Inject an element.
 */
@Composable
inline fun <reified T>injectElement(): T {
    return koinInject<T>()
}