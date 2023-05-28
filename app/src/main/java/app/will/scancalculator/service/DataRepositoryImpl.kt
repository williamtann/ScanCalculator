package app.will.scancalculator.service

import android.app.Application
import app.will.scancalculator.gateway.DataRepository
import app.will.scancalculator.local.dao.CalculationDao
import app.will.scancalculator.local.entity.CalculationEnt
import app.will.scancalculator.manager.DataStoreManager
import app.will.scancalculator.model.Calculation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val app: Application,
    private val calculationDao: CalculationDao,
    private val dataStoreManager: DataStoreManager
) : DataRepository {

    override suspend fun saveCalculationRoom(calculation: Calculation) {
        calculationDao.insertCalculation(with(calculation) {
            CalculationEnt(formula, result)
        })
    }

    override suspend fun loadCalculationsRoom(): Flow<List<Calculation>> {
        return calculationDao.getCalculationList().map {
            it.map { ent ->
                Calculation(ent.formula, ent.result)
            }
        }
    }

    override suspend fun saveCalculationFile(calculations: List<Calculation>) {
        val gson = Gson()
        dataStoreManager.setSecuredData(gson.toJson(calculations))
    }

    override suspend fun loadCalculationsFile(): Flow<List<Calculation>> {
        return dataStoreManager.getSecuredData().map {
            if (it.isNotEmpty()) {
                val gson = Gson()
                val listType = object : TypeToken<List<Calculation>>() {}.type
                gson.fromJson(it, listType)
            } else {
                emptyList()
            }
        }
    }
}