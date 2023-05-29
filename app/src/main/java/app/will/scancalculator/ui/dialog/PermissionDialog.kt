package app.will.scancalculator.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.will.scancalculator.R

@Composable
fun PermissionDialog(
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = onDismiss, buttons = {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider()
            Text(text = if (isPermanentlyDeclined) {
                stringResource(R.string.grant_permission)
            } else {
                stringResource(R.string.ok)
            },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOkClick()
                        }
                    }
                    .padding(16.dp))
        }
    }, title = {
        Text(text = stringResource(R.string.permission_required))
    }, text = {
        Text(
            text = if (isPermanentlyDeclined) {
                stringResource(R.string.permanently_declined)
            } else {
                stringResource(R.string.permission_reason)
            }
        )
    }, modifier = modifier
    )
}