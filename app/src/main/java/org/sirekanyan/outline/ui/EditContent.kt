package org.sirekanyan.outline.ui

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.sirekanyan.outline.R
import org.sirekanyan.outline.Res
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.ext.rememberStateScope
import org.sirekanyan.outline.rememberResources

interface EditDelegate {
    suspend fun onEdited(newValue: String)
}

@Composable
fun rememberEditState(router: Router, delegate: EditDelegate): EditState {
    val scope = rememberStateScope()
    val resources = rememberResources()
    return remember { EditState(scope, router, delegate, resources) }
}

class EditState(
    scope: CoroutineScope,
    private val router: Router,
    private val editDelegate: EditDelegate,
    private val resources: Res,
) : CoroutineScope by scope {

    var error by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun onSaveClicked(newName: String) {
        launch {
            try {
                isLoading = true
                editDelegate.onEdited(newName)
                router.dialog = null
            } catch (exception: Exception) {
                exception.printStackTrace()
                error = resources.getString(R.string.outln_error_check_name)
            } finally {
                isLoading = false
            }
        }
    }

}

@Composable
fun EditContent(
    state: EditState,
    router: Router,
    @StringRes dialogTitle: Int,
    @StringRes textFieldLabel: Int,
    link: String?,
    initialName: String,
    defaultName: String,
) {
    var draft by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialName, TextRange(Int.MAX_VALUE)))
    }
    val context = LocalContext.current
    Column {
        DialogToolbar(
            title = dialogTitle,
            onCloseClick = { router.dialog = null },
            action = R.string.outln_action_save to {
                val newName = draft.text.ifBlank { defaultName }
                state.onSaveClicked(newName)
            },
            isLoading = state.isLoading,
        )
        val focusRequester = remember { FocusRequester() }
        if (link != null) {
            Text(
                text = link,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable(enabled = true) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                    }
            )
        }
        OutlinedTextField(
            value = draft,
            onValueChange = {
                draft = it.copy(text = it.text.trim('\n'))
                state.error = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .focusRequester(focusRequester),
            label = { Text(stringResource(textFieldLabel)) },
            placeholder = { Text(defaultName) },
            isError = state.error.isNotEmpty(),
            supportingText = { Text(state.error) },
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
            maxLines = 4,
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
