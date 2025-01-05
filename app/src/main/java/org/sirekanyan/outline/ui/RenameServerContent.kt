package org.sirekanyan.outline.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.sirekanyan.outline.R
import org.sirekanyan.outline.Router
import org.sirekanyan.outline.SelectedPage
import org.sirekanyan.outline.api.model.Server
import org.sirekanyan.outline.app
import org.sirekanyan.outline.repository.ServerRepository

@Composable
private fun rememberRenameServerDelegate(router: Router, server: Server): EditDelegate {
    val context = LocalContext.current
    val servers = remember { context.app().serverRepository }
    return remember(server) { RenameServerDelegate(router, servers, server) }
}

private class RenameServerDelegate(
    private val router: Router,
    private val servers: ServerRepository,
    private val server: Server,
) : EditDelegate {
    override suspend fun onEdited(newValue: String) {
        val newServer = servers.renameServer(server, newValue)
        router.page = SelectedPage(newServer)
    }
}

@Composable
fun RenameServerContent(router: Router, server: Server) {
    val delegate = rememberRenameServerDelegate(router, server)
    val state = rememberEditState(router, delegate)
    EditContent(
        state,
        router,
        R.string.outln_title_edit_server,
        R.string.outln_label_name,
        server.id,
        server.name,
        server.getHost()
    )
}
