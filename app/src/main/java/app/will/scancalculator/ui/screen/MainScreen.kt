package app.will.scancalculator.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.hilt.navigation.compose.hiltViewModel
import app.will.scancalculator.Const
import app.will.scancalculator.MainViewModel
import app.will.scancalculator.model.Calculation
import app.will.scancalculator.model.DataSourceType
import app.will.scancalculator.ui.dialog.CameraPermissionTextProvider
import app.will.scancalculator.ui.dialog.PermissionDialog
import app.will.scancalculator.ui.screen.destinations.CalculationScreenDestination
import app.will.scancalculator.ui.screen.destinations.CameraScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.will.scancalculator.BuildConfig

@Destination(start = true)
@Composable
fun MainScreen(
    navigator: DestinationsNavigator
) {
    val viewModel = hiltViewModel<MainViewModel>()
    val activity = LocalContext.current as Activity
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri -> navigator.navigate(CalculationScreenDestination(uri.toString())) })
    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                navigator.navigate(CameraScreenDestination())
            } else {
                viewModel.onPermissionResult(
                    permission = Manifest.permission.CAMERA
                )
            }
        })

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        val dataSourceType = viewModel.dataSourceType.collectAsState()
        val state = viewModel.calculations.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Button(onClick = {
                when (BuildConfig.MEDIA_SOURCE) {
                    Const.MediaSource.FILE.name -> {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }

                    Const.MediaSource.CAMERA.name -> {
                        cameraPermissionResultLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                }
            }) {
                Text(text = "Add Calculation")
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                viewModel.updateDataSource(DataSourceType.ROOM_DATABASE)
            }) {
                RadioButton(
                    selected = dataSourceType.value == DataSourceType.ROOM_DATABASE,
                    onClick = {
                        viewModel.updateDataSource(DataSourceType.ROOM_DATABASE)
                    },
                )

                Text(
                    text = "Room Database", modifier = Modifier.fillMaxWidth()
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                viewModel.updateDataSource(DataSourceType.DATA_STORE_FILE)
            }) {
                RadioButton(
                    selected = dataSourceType.value == DataSourceType.DATA_STORE_FILE,
                    onClick = {
                        viewModel.updateDataSource(DataSourceType.DATA_STORE_FILE)
                    },
                )
                Text(
                    text = "Local File", modifier = Modifier.fillMaxWidth()
                )
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.value.size) { index ->
                    CalculationItem(state.value.reversed()[index])
                }
            }
        }
    }

    dialogQueue.forEach { permission ->
        PermissionDialog(permissionTextProvider = when (permission) {
            Manifest.permission.CAMERA -> {
                CameraPermissionTextProvider()
            }

            else -> return@forEach
        }, isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
            activity, permission
        ), onDismiss = viewModel::dismissDialog, onOkClick = {
            viewModel.dismissDialog()
            cameraPermissionResultLauncher.launch(
                Manifest.permission.CAMERA
            )
        }, onGoToAppSettingsClick = {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.packageName, null)
            ).also(activity::startActivity)
        })
    }
}

@Composable
fun CalculationItem(calculation: Calculation) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = calculation.formula,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Text(
            text = calculation.result,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )
    }
}