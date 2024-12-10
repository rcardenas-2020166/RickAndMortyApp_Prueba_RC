package com.rodrigocardenas.rickmortyapp.core

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Utils @Inject constructor() {

    @Composable
    fun ShowInfoDialog(
        message: String,
        onDismiss: () -> Unit
    ) {
        val openDialog = remember { mutableStateOf(true) }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                    onDismiss()
                },
                confirmButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                        onDismiss()
                    }) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                },
                text = {
                    Text(text = message)
                }
            )
        }
    }
}