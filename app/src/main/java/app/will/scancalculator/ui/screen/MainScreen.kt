package app.will.scancalculator.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
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
import app.will.scancalculator.model.Calculation
import app.will.scancalculator.model.DataSourceType
import app.will.scancalculator.ui.dialog.CameraPermissionTextProvider
import app.will.scancalculator.ui.dialog.PermissionDialog
import app.will.scancalculator.ui.screen.destinations.CameraScreenDestination
import app.will.scancalculator.util.CalculationUtil.getCalculationFromTextBlocks
import app.will.scancalculator.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.will.scancalculator.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

@Destination(start = true)
@Composable
fun MainScreen(
    navigator: DestinationsNavigator, resultRecipient: ResultRecipient<CameraScreenDestination, Uri>
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<MainViewModel>()
    val activity = LocalContext.current as Activity
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val photoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                uri?.let {
                    detectText(context, it, recognizer, viewModel, coroutineScope, scaffoldState)
                }
            })
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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Value -> {
                detectText(
                    context, result.value, recognizer, viewModel, coroutineScope, scaffoldState
                )
            }

            is NavResult.Canceled -> {}
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.offset(x = (-8).dp, y = (-8).dp), onClick = {
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
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { scaffoldPadding ->
        val dataSourceType = viewModel.dataSourceType.collectAsState()
        val state = viewModel.calculations.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            DataSourceOption(DataSourceType.ROOM_DATABASE, viewModel, dataSourceType)
            DataSourceOption(DataSourceType.DATA_STORE_FILE, viewModel, dataSourceType)
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
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
fun DataSourceOption(
    type: DataSourceType, viewModel: MainViewModel, dataSourceType: State<DataSourceType>
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
        viewModel.updateDataSource(type)
    }) {
        RadioButton(
            selected = dataSourceType.value == type,
            onClick = {
                viewModel.updateDataSource(type)
            },
        )
        Text(
            text = type.getLabel(), modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CalculationItem(calculation: Calculation) {
    Column(
        modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Input: ${calculation.formula}", fontSize = 16.sp, color = Color.Black
        )
        Text(
            text = "Result: ${calculation.result}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

private fun DataSourceType.getLabel(): String {
    return when (this) {
        DataSourceType.ROOM_DATABASE -> "Room Database"
        DataSourceType.DATA_STORE_FILE -> "Local File"
    }
}

private fun detectText(
    context: Context,
    uri: Uri,
    recognizer: TextRecognizer,
    viewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image).addOnSuccessListener { visionText ->
            val calculation = visionText.textBlocks.getCalculationFromTextBlocks()
            if (calculation != null) {
                viewModel.saveCalculation(calculation)
                showSnackbar(
                    coroutineScope,
                    scaffoldState,
                    "Calculation (${calculation.formula}=${calculation.result}) saved!"
                )
            } else {
                showSnackbar(coroutineScope, scaffoldState, "Failed to detect formula from image!")
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
            showSnackbar(coroutineScope, scaffoldState, "Failed to detect formula from image!")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        showSnackbar(coroutineScope, scaffoldState, "Failed to detect formula from image!")
    }
}

private fun showSnackbar(
    coroutineScope: CoroutineScope, scaffoldState: ScaffoldState, content: String
) {
    coroutineScope.launch {
        scaffoldState.snackbarHostState.showSnackbar(content)
    }
}