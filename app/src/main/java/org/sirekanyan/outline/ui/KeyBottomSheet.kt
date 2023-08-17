package org.sirekanyan.outline.ui

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.ui.icons.IconCopy

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun KeyBottomSheet(key: Key, onDismissRequest: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val localClipboard = LocalClipboardManager.current
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RectangleShape,
        dragHandle = {},
        windowInsets = WindowInsets(0.dp),
    ) {
        Column(Modifier.navigationBarsPadding().padding(top = 4.dp)) {
            Text(
                text = key.accessKey.nameOrDefault,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelLarge,
            )
            ListItem(
                headlineContent = { Text("Copy") },
                leadingContent = { Icon(IconCopy, null) },
                modifier = Modifier.clickable {
                    localClipboard.setText(AnnotatedString(key.accessKey.accessUrl))
                    Toast.makeText(localContext, "Copied", LENGTH_SHORT).show()
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        onDismissRequest()
                    }
                },
            )
        }
    }
}
