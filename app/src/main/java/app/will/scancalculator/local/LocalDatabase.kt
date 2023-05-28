package app.will.scancalculator.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.will.scancalculator.local.dao.CalculationDao
import app.will.scancalculator.local.entity.CalculationEnt

@Database(
    entities = [CalculationEnt::class], version = 1
)
abstract class LocalDatabase : RoomDatabase() {

    abstract val calculationDao: CalculationDao
}