package app.will.scancalculator.model

import kotlinx.serialization.Serializable

@Serializable
data class Calculation(
    val formula: String,
    val result: String
)
