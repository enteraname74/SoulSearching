package com.github.enteraname74.soulsearching.composables.bottomsheets

import com.github.enteraname74.soulsearching.domain.model.Cover

data class BottomSheetTopInformation(
    val title: String = "",
    val subTitle: String? = null,
    val cover: Cover? = null,
)
