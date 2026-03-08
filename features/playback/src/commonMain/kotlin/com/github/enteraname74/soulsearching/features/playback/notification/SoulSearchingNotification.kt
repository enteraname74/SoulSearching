package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.soulsearching.features.playback.model.UpdateData

interface SoulSearchingNotification {
    suspend fun update(updateData: UpdateData)
    fun dismiss()
}