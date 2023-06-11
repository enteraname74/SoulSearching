package com.github.soulsearching.states

import com.github.soulsearching.database.model.ImageCover

data class ImageCoverState(
    val covers : ArrayList<ImageCover> = ArrayList()
)