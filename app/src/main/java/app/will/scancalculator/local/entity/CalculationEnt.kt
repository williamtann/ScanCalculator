package app.will.scancalculator.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class CalculationEnt(
    val formula: String,
    val result: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
