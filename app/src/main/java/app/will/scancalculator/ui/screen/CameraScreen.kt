package app.will.scancalculator.ui.screen

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import app.will.scancalculator.Const.FULL_TIME_FORMAT
import app.will.scancalculator.Const.JPG_FORMAT
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.will.scancalculator.R
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Destination
@Composable
fun CameraScreen(
    resultNavigator: ResultBackNavigator<Uri>
) {
    val mainScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember {
        ImageCapture.Builder().build()
    }
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    LaunchedEffect(Unit) {
        val cameraProvider = suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(context).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(context))
            }
        }
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, preview, imageCapture
        )
        camera.cameraControl.setZoomRatio(0f)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            IconButton(
                onClick = {
                    imageCapture.takePicture(context, onImageCaptured = { uri ->
                        mainScope.launch {
                            resultNavigator.navigateBack(result = uri)
                        }
                    }, onError = { it.printStackTrace() })
                },
                modifier = Modifier
                    .size(64.dp)
                    .padding(1.dp)
                    .border(1.dp, Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.Add, contentDescription = "Take Picture", tint = Color.White
                )
            }
        }
    }
}

fun ImageCapture.takePicture(
    context: Context, onImageCaptured: (Uri) -> Unit, onError: (ImageCaptureException) -> Unit
) {
    val outputDirectory = context.getOutputDirectory()
    val photoFile = File(
        outputDirectory, SimpleDateFormat(
            FULL_TIME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + JPG_FORMAT
    )
    val outputFileOptions =
        ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(ImageCapture.Metadata())
            .build()

    this.takePicture(outputFileOptions,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                val mimeType =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(savedUri.toFile().extension)
                MediaScannerConnection.scanFile(
                    context, arrayOf(savedUri.toFile().absolutePath), arrayOf(mimeType)
                ) { _, _ -> }
                onImageCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        })
}

fun Context.getOutputDirectory(): File {
    val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
        File(it, this.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else this.filesDir
}