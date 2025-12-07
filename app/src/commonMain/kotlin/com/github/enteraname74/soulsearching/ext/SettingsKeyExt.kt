package com.github.enteraname74.soulsearching.ext

import com.github.enteraname74.domain.model.settings.SoulSearchingSettingElement
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.model.settings.settingElementOf
import com.github.enteraname74.soulsearching.theme.ColorPaletteSeed

val SoulSearchingSettingsKeys.ColorTheme.COLOR_PALETTE_SEED: SoulSearchingSettingElement<String>
    get() = settingElementOf(
        key = "COLOR_PALETTE_SEED",
        defaultValue = ColorPaletteSeed.Default.toString(),
    )