package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetDefaults
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheetHandler
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import qrscanner.CameraLens
import qrscanner.QrScanner

class SettingsCloudQrCodeReaderBottomSheet(
    private val onClose: () -> Unit,
    private val onFailure: (String) -> Unit,
    private val onCodeRetrieved: (String) -> Unit,
): SoulBottomSheet {

    @Composable
    override fun BottomSheet() {
        SoulBottomSheetHandler(
            onClose = onClose,
            colors = SoulBottomSheetDefaults.primaryColors(),
        ) { closeWithAnim ->
            BottomSheetContent(
                closeWithAnim = closeWithAnim,
            )
        }
    }

    @Composable
    private fun BottomSheetContent(
        closeWithAnim: () -> Unit,
    ) {
        SoulScreen {
            Column {
                SoulTopBar(
                    title = strings.cloudSignInScanCode,
                    rightAction = null,
                    leftAction = TopBarNavigationAction(
                        onClick = closeWithAnim,
                    )
                )
                QrScanner(
                    modifier = Modifier
                        .fillMaxSize(),
                    flashlightOn = false,
                    cameraLens = CameraLens.Back,
                    openImagePicker = false,
                    onCompletion = { code ->
                        onCodeRetrieved(code)
                        closeWithAnim()
                    },
                    onFailure = onFailure,
                    imagePickerHandler = { _ -> },
                )
            }
        }
    }

}