package app.will.scancalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.will.scancalculator.manager.DataStoreManager
import app.will.scancalculator.model.Calculation
import app.will.scancalculator.model.DataSourceType
import app.will.scancalculator.ui.theme.ScanCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var dataStore: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanCalculatorTheme {
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
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
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

                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
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
            }
        }
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