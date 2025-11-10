package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.features.serialization.SerializationUtils

abstract class CoverFolderRetrieverViewModelDelegate(
    private val settings: SoulSearchingSettings,
    private val loadingManager: LoadingManager,
): CoverFolderRetrieverActions {

    var coverFolderRetriever: CoverFolderRetriever = CoverFolderRetriever.default
    abstract val settingsKey: String

    override val whiteSpaceReplacementTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = REPLACEMENT_ID,
        initialValue = deserializeCoverFolderRetriever(settings.get(SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER))
            .whiteSpaceRule.replacement,
        getLabel = { strings.coverFolderRetrieverRulesReplacement },
        getError = { null },
        onChange = { updateWhiteSpaceReplacement(it) },
        isValid = { true },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    override val coverFileNameTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = COVER_FILE_NAME_ID,
        initialValue = deserializeCoverFolderRetriever(settings.get(SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER))
            .coverFileName.orEmpty(),
        getLabel = { strings.coverFolderRetrieverDynamicFileTitle },
        getError = { strings.fieldCannotBeEmpty },
        onChange = { updateCoverFileName(it) },
        isValid = { it.isNotBlank() },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    override val extensionTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = EXTENSION_ID,
        initialValue = deserializeCoverFolderRetriever(settings.get(SoulSearchingSettingsKeys.Cover.ARTIST_COVER_FOLDER_RETRIEVER))
            .fileExtension.orEmpty(),
        getLabel = { strings.coverFolderRetrieverFileExtension },
        getError = { strings.fieldCannotBeEmpty },
        onChange = { updateFileExtension(it) },
        isValid = { it.isNotBlank() },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    fun deserializeCoverFolderRetriever(json: String): CoverFolderRetriever =
        json.takeIf { it.isNotBlank() }?.let {
            SerializationUtils.deserialize(it)
        } ?: CoverFolderRetriever.default

    abstract suspend fun handleToggleActivation(isActivated: Boolean)

    override fun onToggleActivation() {
        loadingManager.withLoadingOnIO {
            val newIsActivated = !coverFolderRetriever.isActivated
            settings.set(
                key = settingsKey,
                value = SerializationUtils.serialize(
                    coverFolderRetriever.copy(
                        isActivated = newIsActivated,
                    )
                )
            )
            handleToggleActivation(newIsActivated)
        }
    }

    override fun updateFolderModePath(newPath: String) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    folderModePath = newPath,
                )
            )
        )
    }

    override fun updateFileModePath(newPath: String) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    fileModePath = newPath,
                )
            )
        )
    }

    override fun updateCoverFileName(newName: String) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    coverFileName = newName,
                )
            )
        )
    }

    override fun updateFileExtension(newExtension: String) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    fileExtension = newExtension,
                )
            )
        )
    }

    override fun toggleWhiteSpace() {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    whiteSpaceRule = coverFolderRetriever.whiteSpaceRule.copy(
                        isActivated = !coverFolderRetriever.whiteSpaceRule.isActivated,
                    )
                )
            )
        )
    }

    override fun updateWhiteSpaceReplacement(replacement: String) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    whiteSpaceRule = coverFolderRetriever.whiteSpaceRule.copy(
                        replacement = replacement,
                    ),
                )
            )
        )
    }

    override fun updateLowerCase(isLowerCase: Boolean?) {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    lowerCaseRule = isLowerCase,
                )
            )
        )
    }

    override fun toggleMode() {
        settings.set(
            key = settingsKey,
            value = SerializationUtils.serialize(
                coverFolderRetriever.copy(
                    mode = when(coverFolderRetriever.mode) {
                        CoverFolderRetriever.DynamicMode.Folder -> CoverFolderRetriever.DynamicMode.File
                        CoverFolderRetriever.DynamicMode.File -> CoverFolderRetriever.DynamicMode.Folder
                    }
                )
            )
        )
    }

    private companion object {
        const val COVER_FILE_NAME_ID = "COVER_FILE_NAME"
        const val REPLACEMENT_ID = "REPLACEMENT_ID"
        const val EXTENSION_ID = "EXTENSION_ID"
    }
}