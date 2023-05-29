package app.will.scancalculator.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.will.scancalculator.gateway.DataRepository
import app.will.scancalculator.model.Calculation
import app.will.scancalculator.model.DataSourceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String
    ) {
        if (!visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    private val _dataSourceType = MutableStateFlow(DataSourceType.ROOM_DATABASE)
    val dataSourceType = _dataSourceType.asStateFlow()
    private val _calculationsRoom = MutableStateFlow(emptyList<Calculation>())
    private val _calculationsFile = MutableStateFlow(emptyList<Calculation>())

    val calculations = combine(
        _calculationsRoom, _calculationsFile, _dataSourceType
    ) { calculationsRoom, calculationsFile, dataSourceType ->
        when (dataSourceType) {
            DataSourceType.ROOM_DATABASE -> {
                calculationsRoom
            }

            DataSourceType.DATA_STORE_FILE -> {
                calculationsFile
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            dataRepository.loadCalculationsRoom().collectLatest {
                _calculationsRoom.value = it
            }
        }
        viewModelScope.launch {
            dataRepository.loadCalculationsFile().collect {
                _calculationsFile.value = it
            }
        }
    }

    fun updateDataSource(dataSourceType: DataSourceType) {
        _dataSourceType.value = dataSourceType
    }

    fun saveCalculation(calculation: Calculation) {
        when (_dataSourceType.value) {
            DataSourceType.ROOM_DATABASE -> {
                viewModelScope.launch {
                    dataRepository.saveCalculationRoom(calculation)
                }
            }

            DataSourceType.DATA_STORE_FILE -> {
                viewModelScope.launch {
                    dataRepository.saveCalculationFile(
                        _calculationsFile.value.toMutableList().apply {
                            add((calculation))
                        })
                }
            }
        }
    }
}
