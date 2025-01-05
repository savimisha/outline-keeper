package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.R
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.api.model.Key
import org.sirekanyan.outline.app
import org.sirekanyan.outline.ext.addPrefixToKey
import org.sirekanyan.outline.repository.KeyRepository

@Composable
private fun rememberRenameKeyDelegate(key: Key): EditDelegate {
    val context = LocalContext.current
    val keys = remember { context.app().keyRepository }
    return remember(key) { RenameKeyDelegate(keys, key) }
}

private class RenameKeyDelegate(
    private val keys: KeyRepository,
    private val key: Key,
) : EditDelegate {
    override suspend fun onEdited(newValue: String) {
        keys.renameKey(key.server, key, newValue)
        try {
            keys.updateKeys(key.server)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}

@Composable
fun RenameKeyContent(router: Router, key: Key, prefix: String) {
    val delegate = rememberRenameKeyDelegate(key)
    val state = rememberEditState(router, delegate)
    EditContent(
        state,
        router,
        R.string.outln_title_edit_key,
        R.string.outln_label_name,
        addPrefixToKey(key.accessUrl, prefix),
        key.name,
        key.defaultName
    )
}
