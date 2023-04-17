package com.github.soulsearching

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.github.soulsearching.composables.bottomSheets.TestBottomSheet
import com.github.soulsearching.ui.theme.SoulSearchingTheme

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoulSearchingTheme {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val constraintsScope = this
                    val maxHeight = with(LocalDensity.current) {
                        constraintsScope.maxHeight.toPx()
                    }

                    TestBottomSheet(maxHeight = maxHeight)
                    LazyColumn(
                        Modifier
                            .background(MaterialTheme.colorScheme.secondary),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Text(text = "texte caché")
                        }
                    }
                }
            }
        }
    }
}
