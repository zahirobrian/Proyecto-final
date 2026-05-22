package com.ipn.miaumission.utils

import com.ipn.miaumission.model.MathQuestion
import com.ipn.miaumission.model.Operation
import com.ipn.miaumission.model.QuestionType
import kotlin.random.Random

/**
 * Genera preguntas matemáticas aleatorias según la operación del nivel.
 * Los 3 tipos se alternan aleatoriamente: respuesta abierta, opción múltiple, verdadero/falso.
 */
object QuestionGenerator {

    fun generate(operation: Operation): MathQuestion {
        val type = QuestionType.values().random()
        return when (operation) {
            Operation.SUMA           -> generateSum(type)
            Operation.RESTA         -> generateSubtraction(type)
            Operation.MULTIPLICACION -> generateMultiplication(type)
        }
    }

    private fun generateSum(type: QuestionType): MathQuestion {
        val a = Random.nextInt(1, 20)
        val b = Random.nextInt(1, 20)
        val answer = a + b
        val display = "$a + $b = ?"
        return buildQuestion(display, answer, type)
    }

    private fun generateSubtraction(type: QuestionType): MathQuestion {
        val b = Random.nextInt(1, 15)
        val a = Random.nextInt(b, 30)   // a >= b para resultado positivo
        val answer = a - b
        val display = "$a - $b = ?"
        return buildQuestion(display, answer, type)
    }

    private fun generateMultiplication(type: QuestionType): MathQuestion {
        val a = Random.nextInt(2, 10)
        val b = Random.nextInt(2, 10)
        val answer = a * b
        val display = "$a × $b = ?"
        return buildQuestion(display, answer, type)
    }

    private fun buildQuestion(display: String, answer: Int, type: QuestionType): MathQuestion {
        return when (type) {
            QuestionType.OPEN -> MathQuestion(display, answer, type)

            QuestionType.MULTIPLE_CHOICE -> {
                // Genera 3 distractores únicos distintos de la respuesta correcta
                val distractors = mutableSetOf<Int>()
                while (distractors.size < 3) {
                    val d = answer + Random.nextInt(-10, 11)
                    if (d != answer && d > 0) distractors.add(d)
                }
                val opts = (distractors.toList() + answer).shuffled()
                MathQuestion(display, answer, type, options = opts)
            }

            QuestionType.TRUE_FALSE -> {
                // 50% de las veces la afirmación es correcta, 50% incorrecta
                val isTrue = Random.nextBoolean()
                val shownAnswer = if (isTrue) answer else {
                    var wrong = answer + Random.nextInt(-8, 9)
                    while (wrong == answer || wrong < 0) wrong = answer + Random.nextInt(-8, 9)
                    wrong
                }
                val stmt = display.replace("?", "$shownAnswer")
                MathQuestion(display, answer, type,
                    tfStatement = stmt,
                    tfCorrectIsTrue = isTrue)
            }
        }
    }
}
