package com.github.enteraname74.soulsearching.composables.bottomsheets

import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.domain.model.Cover

data class BottomSheetTopInformation(
    val title: SoulTextSpec = "".toTextSpec(),
    val subTitle: SoulTextSpec? = null,
    val cover: Cover? = null,
)
