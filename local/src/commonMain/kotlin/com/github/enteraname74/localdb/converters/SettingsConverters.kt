package com.github.enteraname74.localdb.converters

import androidx.room3.ColumnTypeConverter
import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.model.SortDirection
import com.github.enteraname74.soulsearching.domain.model.SortType
import com.github.enteraname74.soulsearching.domain.model.settings.ColorThemeType
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingTheme

internal object SettingsConverters {
    @ColumnTypeConverter fun sortTypeToInt(value: SortType) = value.value
    @ColumnTypeConverter fun intToSortType(value: Int) = SortType.from(value) ?: SortType.DEFAULT
    @ColumnTypeConverter fun sortDirectionToInt(value: SortDirection) = value.value
    @ColumnTypeConverter fun intToSortDirection(value: Int) = SortDirection.from(value) ?: SortDirection.DEFAULT
    @ColumnTypeConverter fun colorThemeTypeToInt(value: ColorThemeType) = value.value
    @ColumnTypeConverter fun intToColorThemeType(value: Int) = ColorThemeType.fromOrDefault(value)
    @ColumnTypeConverter fun themeToString(value: SoulSearchingTheme) = value.value
    @ColumnTypeConverter fun stringToTheme(value: String) = SoulSearchingTheme.fromOrDefault(value)
    @ColumnTypeConverter fun coverModeToString(value: CoverFolderRetriever.DynamicMode) = value.name
    @ColumnTypeConverter fun stringToCoverMode(value: String) = CoverFolderRetriever.DynamicMode.entries.firstOrNull { it.name == value } ?: CoverFolderRetriever.DynamicMode.Folder
}
