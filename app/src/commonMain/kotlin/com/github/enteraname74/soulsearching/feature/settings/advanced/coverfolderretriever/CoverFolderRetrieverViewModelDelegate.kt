package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever

import com.github.enteraname74.soulsearching.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldDefaults
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolderImpl
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class CoverFolderRetrieverViewModelDelegate(
    private val settingsRepository: SettingsRepository,
    private val loadingManager: LoadingManager,
    private val workDispatcher: WorkDispatcher,
): CoverFolderRetrieverActions {

    var coverFolderRetriever: CoverFolderRetriever = CoverFolderRetriever.default
    private val scope = CoroutineScope(workDispatcher.dispatcher)

    override val whiteSpaceReplacementTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = REPLACEMENT_ID,
        initialValue = CoverFolderRetriever.default.whiteSpaceRule.replacement,
        getLabel = { strings.coverFolderRetrieverRulesReplacement },
        getError = { null },
        onChange = { updateWhiteSpaceReplacement(it) },
        isValid = { true },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    override val coverFileNameTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = COVER_FILE_NAME_ID,
        initialValue = CoverFolderRetriever.default.coverFileName.orEmpty(),
        getLabel = { strings.coverFolderRetrieverDynamicFileTitle },
        getError = { strings.fieldCannotBeEmpty },
        onChange = { updateCoverFileName(it) },
        isValid = { it.isNotBlank() },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    override val extensionTextField: SoulTextFieldHolder = SoulTextFieldHolderImpl(
        id = EXTENSION_ID,
        initialValue = CoverFolderRetriever.default.fileExtension.orEmpty(),
        getLabel = { strings.coverFolderRetrieverFileExtension },
        getError = { strings.fieldCannotBeEmpty },
        onChange = { updateFileExtension(it) },
        isValid = { it.isNotBlank() },
        style = SoulTextFieldStyle.Unique,
        getColors = { SoulTextFieldDefaults.primaryColors() },
    )

    abstract suspend fun handleToggleActivation(isActivated: Boolean)

    private suspend fun save(value: CoverFolderRetriever) {
        coverFolderRetriever = value
        settingsRepository.setArtistCoverFolderRetriever(value)
    }

    fun sync(value: CoverFolderRetriever) {
        coverFolderRetriever = value
        whiteSpaceReplacementTextField.updateValue(value.whiteSpaceRule.replacement)
        coverFileNameTextField.updateValue(value.coverFileName.orEmpty())
        extensionTextField.updateValue(value.fileExtension.orEmpty())
    }

    override fun onToggleActivation() {
        loadingManager.withLoadingOnScope(CoroutineScope(workDispatcher.dispatcher)) {
            val newIsActivated = !coverFolderRetriever.isActivated
            save(coverFolderRetriever.copy(isActivated = newIsActivated))
            handleToggleActivation(newIsActivated)
        }
    }

    override fun updateFolderModePath(newPath: String) {
        scope.launch { save(coverFolderRetriever.copy(folderModePath = newPath)) }
    }

    override fun updateFileModePath(newPath: String) {
        scope.launch { save(coverFolderRetriever.copy(fileModePath = newPath)) }
    }

    override fun updateCoverFileName(newName: String) {
        scope.launch { save(coverFolderRetriever.copy(coverFileName = newName)) }
    }

    override fun updateFileExtension(newExtension: String) {
        scope.launch { save(coverFolderRetriever.copy(fileExtension = newExtension)) }
    }

    override fun toggleWhiteSpace() {
        scope.launch { save(coverFolderRetriever.copy(whiteSpaceRule = coverFolderRetriever.whiteSpaceRule.copy(isActivated = !coverFolderRetriever.whiteSpaceRule.isActivated))) }
    }

    override fun updateWhiteSpaceReplacement(replacement: String) {
        scope.launch { save(coverFolderRetriever.copy(whiteSpaceRule = coverFolderRetriever.whiteSpaceRule.copy(replacement = replacement))) }
    }

    override fun updateLowerCase(isLowerCase: Boolean?) {
        scope.launch { save(coverFolderRetriever.copy(lowerCaseRule = isLowerCase)) }
    }

    override fun toggleMode() {
        scope.launch {
            save(coverFolderRetriever.copy(mode = when(coverFolderRetriever.mode) {
                CoverFolderRetriever.DynamicMode.Folder -> CoverFolderRetriever.DynamicMode.File
                CoverFolderRetriever.DynamicMode.File -> CoverFolderRetriever.DynamicMode.Folder
            }))
        }
    }

    private companion object {
        const val COVER_FILE_NAME_ID = "COVER_FILE_NAME"
        const val REPLACEMENT_ID = "REPLACEMENT_ID"
        const val EXTENSION_ID = "EXTENSION_ID"
    }
}
