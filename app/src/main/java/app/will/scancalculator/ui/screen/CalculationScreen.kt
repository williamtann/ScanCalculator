package app.will.scancalculator.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun CalculationScreen(
    uriString: String
) {
    AsyncImage(
        model = Uri.parse(uriString),
        contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}