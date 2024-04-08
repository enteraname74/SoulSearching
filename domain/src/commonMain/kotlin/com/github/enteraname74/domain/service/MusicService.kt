package com.github.enteraname74.domain.service

import com.github.enteraname74.domain.model.MusicWithCover
import java.util.UUID

/**
 * Service for handling music related work.
 */
interface MusicService {
    /**
     * Save a music.
     */
    suspend fun save(musicWithCover: MusicWithCover)

    /**
     * Delete a music.
     */
    suspend fun delete(musicId: UUID)

    /**
     * Update a music.
     */
    suspend fun update(musicWithCover: MusicWithCover)
}