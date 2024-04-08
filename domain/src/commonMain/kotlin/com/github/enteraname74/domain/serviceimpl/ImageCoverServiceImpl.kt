package com.github.enteraname74.domain.serviceimpl

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.service.ImageCoverService
import java.util.UUID

/**
 * Implementation of the ImageCoverService using the specified repositories.
 */
class ImageCoverServiceImpl(
    private val imageCoverRepository: ImageCoverRepository
) : ImageCoverService {
    override suspend fun save(cover: ImageBitmap): UUID {
        val id = UUID.randomUUID()
        imageCoverRepository.insertImageCover(
            ImageCover(
                coverId = id,
                cover = cover
            )
        )
        return id
    }
}