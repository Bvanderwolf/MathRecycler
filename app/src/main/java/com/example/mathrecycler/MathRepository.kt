package com.example.mathrecycler

import android.util.Log
import kotlin.math.roundToInt
import kotlin.random.Random

class MathRepository {

    private val addingFloatChance = 0.5
    private val chanceOfWrongProposedAnswer = 0.5
    private val wrongAnswerOffset = 5

    //creates a random number based on chosen difficulty and operator difficulty
    fun createRandomNumber(difficulty: String, operator: String) : Number{
        val threshHold = getThreshHold(difficulty, operator)
        var rdnNumber = Random.nextDouble(threshHold.toDouble())
        var rdnInteger = rdnNumber.toInt()
        while(rdnInteger == 0){
            rdnNumber = Random.nextDouble(threshHold.toDouble())
            rdnInteger = rdnNumber.toInt()
        }
        return rdnNumber.toInt()
    }

    fun createDivisionNumber(argOne: Float): Number{
        var divisor = 1
        for(i in 2 until 9){
            if(argOne % i == 0.0f){
                divisor = i
                break
            }
        }
        return divisor
    }

    //returns operator symbol to be showed based on name
    fun getOperatorSymbol(operator: String): String{
        return when(operator){
            "Addition" -> "+"
            "Subtraction" -> "-"
            "Division" -> "/"
            "Multiplication" -> "*"
            "Modulo" -> "%"
            else -> ""
        }
    }

    //returns a threshHold between where a number can be based on difficulty and operator difficulty
    private fun getThreshHold(difficulty: String, operator: String) : Number{
        return when(difficulty){
            "Easy" -> if(isDifficultOperator(operator)) 10 else 100
            "Moderate" -> if(isDifficultOperator(operator)) 20 else 200
            "Hard" -> if(isDifficultOperator(operator)) 50 else 500
            else -> 0
        }
    }

    /*creates a proposed answer to be showed on the math item. Based chanceOfWrongProposedAnswer we do nothing
    or add or subtract a random number between 0 and wrongAnswerOffset based on addingFloatChance*/
    fun createProposedAnswer(argOne: Float, argTwo: Float, operator: String) : Number{
        var addedFloat = 0f
        if(Random.nextFloat() < chanceOfWrongProposedAnswer){
            var addingFloat = Random.nextFloat() > addingFloatChance
            addedFloat += if(addingFloat) ((Random.nextFloat()) * wrongAnswerOffset).roundToInt()
            else (-(Random.nextFloat() * wrongAnswerOffset)).roundToInt()
        }
        return when(operator){
            "+" -> argOne + argTwo + addedFloat
            "-" -> argOne - argTwo + addedFloat
            "/" -> argOne / argTwo + addedFloat
            "*" -> argOne * argTwo + addedFloat
            "%" -> argOne % argTwo + addedFloat
            else -> 0
        }
    }

    //returns whether a given operator is a difficult operator or not
    private fun isDifficultOperator(operator: String) : Boolean = arrayListOf("Division", "Multiplication", "Modulo").contains(operator)
}