package com.github.enteraname74.domain.service

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * Service for handling covers related work.
 */
interface ImageCoverService {
    /**
     * Save a cover.
     *
     * @param cover the cover to save.
     * @return the id of the saved cover.
     */
    suspend fun save(cover: ImageBitmap): UUID
}