package com.github.soulsearching.states

import com.github.enteraname74.domain.model.ImageCover

/**
 * State for managing all image covers.
 */
data class ImageCoverState(
    val covers : ArrayList<ImageCover> = ArrayList()
)