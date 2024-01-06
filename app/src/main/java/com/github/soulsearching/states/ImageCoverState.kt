package com.github.soulsearching.states

import com.github.soulsearching.model.UIImageCover

/**
 * State for managing all image covers.
 */
data class ImageCoverState(
    val covers : ArrayList<UIImageCover> = ArrayList()
)