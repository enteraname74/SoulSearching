package com.github.enteraname74.domain.usecase.cloud

// TODO SYNC: Add progress
interface CloudBackgroundSyncJob {
    suspend fun launchIfPossible()
}