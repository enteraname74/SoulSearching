package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.CloudInscriptionCode
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.dialog.SoulBasicDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextField
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldStyle
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import qrgenerator.qrkitpainter.rememberQrKitPainter

class SettingsCloudCodeDialog(
    private val onDismiss: () -> Unit,
    private val inscriptionCode: CloudInscriptionCode,
) : SoulDialog {

    @Composable
    override fun Dialog() {
        SoulBasicDialog(
            onDismiss = onDismiss,
            modifier = Modifier
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp)
            ) {
                TopBar()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(UiConstants.Spacing.large),
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                ) {
                    QrCode(inscriptionCode = inscriptionCode)
                    CodeTextField(inscriptionCode = inscriptionCode)
                }
            }
        }
    }


    @Composable
    private fun TopBar() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier
                    .size(UiConstants.ImageSize.smallPlus)
            )

            Text(
                text = strings.cloudSignInCode,
                style = UiConstants.Typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            SoulIconButton(
                icon = Icons.Rounded.Cancel,
                onClick = onDismiss,
            )
        }
    }

    @Composable
    private fun CodeTextField(
        inscriptionCode: CloudInscriptionCode,
    ) {
        val clipboardManager = LocalClipboardManager.current

        SoulTextField(
            value = inscriptionCode.code,
            onValueChange = { _ -> },
            error = null,
            isInError = false,
            isActive = false,
            labelName = strings.cloudSignInCode,
            focusManager = LocalFocusManager.current,
            style = SoulTextFieldStyle.Unique,
            isPassword = true,
            leadingIcon = {
                SoulIconButton(
                    icon = Icons.Rounded.ContentCopy,
                    onClick = {
                        clipboardManager.setText(AnnotatedString(inscriptionCode.code))
                    }
                )
            }
        )
    }

    @Composable
    private fun QrCode(
        inscriptionCode: CloudInscriptionCode
    ) {
        var isShown by rememberSaveable {
            mutableStateOf(false)
        }

        val qrCodePainter = rememberQrKitPainter(data = inscriptionCode.code)

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                SoulIconButton(
                    icon = Icons.Rounded.QrCode,
                    onClick = {
                        isShown = !isShown
                    },
                    colors = SoulButtonDefaults.colors(
                        containerColor = if (isShown) {
                            SoulSearchingColorTheme.colorScheme.secondary
                        } else {
                            Color.Transparent
                        },
                        contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
                    )
                )
            }
            AnimatedVisibility(
                visible = isShown
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = qrCodePainter,
                        contentDescription = null,
                        modifier = Modifier
                            .sizeIn(
                                minWidth = 200.dp,
                                minHeight = 200.dp,
                            )
                            .fillMaxSize(0.5f)
                    )
                }
            }
        }
    }
}