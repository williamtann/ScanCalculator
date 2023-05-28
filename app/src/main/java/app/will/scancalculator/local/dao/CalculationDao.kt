package app.will.scancalculator.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import app.will.scancalculator.local.entity.CalculationEnt
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {

    @Insert
    suspend fun insertCalculation(calculationEnt: CalculationEnt)

    @Query("SELECT * FROM CalculationEnt")
    fun getCalculationList(): Flow<List<CalculationEnt>>
}