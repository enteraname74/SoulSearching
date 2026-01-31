package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed

val COLOR_PALETTE_SEED_SETTINGS_ELEMENT: SoulSearchingSettingElement<String>
    get() = settingElementOf(
        key = "COLOR_PALETTE_SEED",
        defaultValue = ColorPaletteSeed.Default.toString(),
    )