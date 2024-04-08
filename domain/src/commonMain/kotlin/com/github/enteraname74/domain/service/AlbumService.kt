package com.github.enteraname74.domain.service

import java.util.UUID

/**
 * Service for handling album related work.
 */
interface AlbumService {
    /**
     * Delete an album from its id.
     */
    suspend fun delete(albumId: UUID)
}