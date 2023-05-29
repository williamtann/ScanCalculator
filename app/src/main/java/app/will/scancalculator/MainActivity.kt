package app.will.scancalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.will.scancalculator.ui.screen.NavGraphs
import app.will.scancalculator.ui.theme.ScanCalculatorTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanCalculatorTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}