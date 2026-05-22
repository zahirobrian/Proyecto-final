package com.ipn.miaumission.model

import com.ipn.miaumission.R

/**
 * Personajes jugables del juego.
 * Cada uno tiene un nombre, drawable de imagen y color de collar.
 */
enum class Character(
    val displayName: String,
    val drawableRes: Int,
    val drawableCollarRes: Int
) {
    CHEETO("Cheeto", R.drawable.cat_cheeto, R.drawable.cat_cheeto_collar),
    PELUSA("Pelusa", R.drawable.cat_pelusa, R.drawable.cat_pelusa_collar),
    MANTECADA("Mantecada", R.drawable.cat_mantecada, R.drawable.cat_mantecada_collar),
    WAFFLE("Waffle", R.drawable.cat_waffle, R.drawable.cat_waffle_collar),
    LUI("Lui", R.drawable.cat_lui, R.drawable.cat_lui_collar)
}

/**
 * Niveles del juego con sus operaciones y metas.
 */
data class Level(
    val number: Int,
    val operation: Operation,
    val timeSeconds: Int,
    val goalCats: Int,
    val totalCats: Int
)

enum class Operation(val label: String) {
    SUMA("Sumas (+)"),
    RESTA("Restas (-)"),
    MULTIPLICACION("Multiplicaciones (×)")
}

val LEVELS = listOf(
    Level(1, Operation.SUMA,           60, 5, 15),
    Level(2, Operation.RESTA,          55, 6, 15),
    Level(3, Operation.MULTIPLICACION, 50, 7, 15)
)

/**
 * Tipos de pregunta matemática.
 */
enum class QuestionType { OPEN, MULTIPLE_CHOICE, TRUE_FALSE }

/**
 * Representa una pregunta matemática generada.
 */
data class MathQuestion(
    val display: String,        // texto a mostrar, ej "3 + 4 = ?"
    val correctAnswer: Int,
    val type: QuestionType,
    val options: List<Int> = emptyList(),   // para opción múltiple
    val tfStatement: String = "",           // para verdadero/falso
    val tfCorrectIsTrue: Boolean = true     // para verdadero/falso
)

/**
 * Un gato en el juego: posición, si tiene collar y qué personaje es.
 */
data class GameCat(
    val id: Int,
    val character: Character,
    val hasCollar: Boolean,
    var x: Float,
    var y: Float,
    var isRescued: Boolean = false,
    var isVisible: Boolean = true
)
