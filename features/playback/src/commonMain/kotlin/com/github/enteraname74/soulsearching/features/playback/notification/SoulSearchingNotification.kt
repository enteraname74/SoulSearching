package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.soulsearching.features.playback.model.UpdateData

interface SoulSearchingNotification {
    suspend fun updateNotification(updateData: UpdateData)
    fun dismissNotification()
}