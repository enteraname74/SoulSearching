package com.github.enteraname74.soulsearching.repository.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val repositoryWorkDispatcher: CoroutineDispatcher = Dispatchers.Default
