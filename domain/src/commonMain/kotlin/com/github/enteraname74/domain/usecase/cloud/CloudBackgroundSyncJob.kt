package com.github.enteraname74.domain.usecase.cloud

interface CloudBackgroundSyncJob {
    suspend fun launchIfPossible()
}