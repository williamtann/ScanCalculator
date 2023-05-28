package app.will.scancalculator.gateway

import app.will.scancalculator.model.Calculation
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun saveCalculationRoom(calculation: Calculation)
    suspend fun loadCalculationsRoom(): Flow<List<Calculation>>
    suspend fun saveCalculationFile(calculations: List<Calculation>)
    suspend fun loadCalculationsFile(): Flow<List<Calculation>>
}