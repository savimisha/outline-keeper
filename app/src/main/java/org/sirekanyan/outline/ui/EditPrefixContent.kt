package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.R
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.app
import org.sirekanyan.outline.db.KeyValueDao
import org.sirekanyan.outline.feature.prefix.Prefix

@Composable
private fun rememberEditPrefixDelegate(): EditDelegate {
    val context = LocalContext.current
    val keyValue = remember { context.app().prefsDao }
    return remember { EditPrefixDelegate(keyValue) }
}

private class EditPrefixDelegate(
    private val keyValue: KeyValueDao,
) : EditDelegate {
    override suspend fun onEdited(newValue: String) {
        keyValue.put(Prefix.KEY, newValue)
    }
}

@Composable
fun EditPrefixContent(router: Router, prefix: String) {
    val delegate = rememberEditPrefixDelegate()
    val state = rememberEditState(router, delegate)
    EditContent(
        state,
        router,
        R.string.outln_title_edit_prefix,
        R.string.outln_label_prefix,
        null,
        prefix,
        Prefix.DEFAULT,
    )
}
