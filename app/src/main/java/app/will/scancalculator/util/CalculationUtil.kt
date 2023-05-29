package app.will.scancalculator.util

import app.will.scancalculator.model.Calculation
import com.google.mlkit.vision.text.Text

object CalculationUtil {

    fun MutableList<Text.TextBlock>.getCalculationFromTextBlocks(): Calculation? {
        val formulaFormat = """^(\d+(?:\.\d+)?)([*/+\-])(\d+(?:\.\d+)?)$""".toRegex()
        val whiteSpace = "\\s".toRegex()
        for (block in this) {
            for (line in block.lines) {
                val text = line.text.replace(whiteSpace, "")
                if (formulaFormat.matches(text)) {
                    formulaFormat.find(text)?.apply {
                        if (groupValues.size == 4) {
                            val formula = groupValues[0]
                            val firstNumber = groupValues[1].toDouble()
                            val secondNumber = groupValues[3].toDouble()
                            val operator = groupValues[2].toCharArray()[0]
                            var result = 0.0
                            when (operator) {
                                '+' -> { result = firstNumber + secondNumber }
                                '-' -> { result = firstNumber - secondNumber }
                                '*' -> { result = firstNumber * secondNumber }
                                '/' -> { result = firstNumber / secondNumber }
                            }
                            return Calculation(formula, result.roundToLastDecimal())
                        }
                    }
                }
            }
        }
        return null
    }

    fun Double.roundToLastDecimal(): String {
        val num = this.toString().toDouble()
        return if (num % 1 == 0.0) {
            num.toInt().toString()
        } else {
            num.toString()
        }
    }
}