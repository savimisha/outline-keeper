package org.sirekanyan.outline.ui

import android.content.Intent
import android.content.Intent.ACTION_SENDTO
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.sirekanyan.outline.BuildConfig
import org.sirekanyan.outline.R
import org.sirekanyan.outline.ext.logDebug
import org.sirekanyan.outline.ext.openGooglePlay
import org.sirekanyan.outline.ext.showToast
import org.sirekanyan.outline.isDebugBuild
import org.sirekanyan.outline.isPlayFlavor
import org.sirekanyan.outline.ui.icons.IconOpenInNew
import org.sirekanyan.outline.ui.icons.IconPlayStore

@Composable
fun AboutDialogContent(onDismiss: () -> Unit) {
    val appName = stringResource(R.string.outln_app_name)
    val appVersion = BuildConfig.VERSION_NAME
    AlertDialog(
        icon = { Icon(Icons.Default.Info, null) },
        title = { Text("$appName $appVersion", textAlign = TextAlign.Center) },
        text = {
            val context = LocalContext.current
            val annotatedString = buildAnnotatedString {
                append("An application for managing Outline VPN servers. ")
                append("You can find more information on ")
                val url = "https://getoutline.org"
                withLink(LinkAnnotation.Url(url = "https://getoutline.org") {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    onDismiss()
                }) {
                    withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                        append("getoutline.org")
                    }
                }
                append("\n\nSource code of this app is open and available on ")
                val url1 = stringResource(R.string.outln_source_code_link)
                withLink(LinkAnnotation.Url(url = url) { _ ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url1)))
                    onDismiss()
                }) {
                    withStyle(SpanStyle(MaterialTheme.colorScheme.primary)) {
                        append(stringResource(R.string.outln_source_code_title))
                    }
                }
            }
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            val textStyle = MaterialTheme.typography.bodyMedium.copy(textColor)
            Text(text = annotatedString, style = textStyle)
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            val context = LocalContext.current
            val clipboard = LocalClipboardManager.current
            AboutItem(Icons.Default.Email, R.string.outln_btn_send_feedback) {
                val email = "outline@sirekanyan.org"
                val subject = Uri.encode("Feedback: $appName $appVersion")
                val intent = Intent(ACTION_SENDTO, Uri.parse("mailto:$email?subject=$subject"))
                try {
                    context.startActivity(intent)
                } catch (exception: Exception) {
                    logDebug("Cannot find email app", exception)
                    clipboard.setText(AnnotatedString(email))
                    context.showToast(R.string.outln_toast_email_copied)
                }
            }
            if (isPlayFlavor() || isDebugBuild()) {
                val packageName = "org.sirekanyan.outline"
                val playUri = "https://play.google.com/store/apps/details?id=$packageName"
                AboutItem(IconPlayStore, R.string.outln_btn_rate_on_play_store) {
                    context.openGooglePlay(playUri)
                    onDismiss()
                }
            }
        },
    )
}

@Composable
private fun AboutItem(icon: ImageVector, @StringRes text: Int, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    ) {
        Icon(icon, null, Modifier.padding(horizontal = 4.dp))
        Text(
            text = stringResource(text),
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(IconOpenInNew, null, Modifier.padding(horizontal = 8.dp))
    }
}
