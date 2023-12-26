package org.sirekanyan.outline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import org.sirekanyan.outline.ui.AddServerContent
import org.sirekanyan.outline.ui.DeleteKeyContent
import org.sirekanyan.outline.ui.DeleteServerContent
import org.sirekanyan.outline.ui.RenameKeyContent
import org.sirekanyan.outline.ui.RenameServerContent
import org.sirekanyan.outline.ui.theme.OutlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val router = rememberRouter()
            val state = rememberMainState(router)
            OutlineTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BackHandler(state.drawer.isOpen) {
                        state.closeDrawer()
                    }
                    MainContent(state)
                    state.dialog?.let { dialog ->
                        BackHandler(state.dialog != null) {
                            state.dialog = null
                        }
                        when (dialog) {
                            is AddServerDialog -> Surface { AddServerContent(router) }
                            is RenameServerDialog -> Surface { RenameServerContent(state, router, dialog.server) }
                            is RenameKeyDialog -> Surface { RenameKeyContent(state, dialog.key) }
                            is DeleteKeyDialog -> {
                                val (key) = dialog
                                DeleteKeyContent(
                                    key = key,
                                    onDismiss = { state.dialog = null },
                                    onConfirm = { state.onDeleteKeyConfirmed(key) }
                                )
                            }
                            is DeleteServerDialog -> {
                                val (server) = dialog
                                DeleteServerContent(
                                    serverName = server.name,
                                    onDismiss = { state.dialog = null },
                                    onConfirm = { state.onDeleteServerConfirmed(server) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
