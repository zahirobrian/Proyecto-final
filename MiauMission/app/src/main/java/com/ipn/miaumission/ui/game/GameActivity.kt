package com.ipn.miaumission.ui.game

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ipn.miaumission.R
import com.ipn.miaumission.databinding.ActivityGameBinding
import com.ipn.miaumission.model.*
import com.ipn.miaumission.ui.result.ResultActivity
import com.ipn.miaumission.utils.QuestionGenerator
import kotlin.random.Random

/**
 * Activity principal del juego MiauMission.
 *
 * Flujo:
 * 1. Aparecen gatos pixel art en el escenario de calle
 * 2. El jugador toca un gato
 * 3. Si tiene collar → notificación breve (no rescatable)
 * 4. Si no tiene collar → aparece pregunta matemática
 * 5. Si responde bien → gato rescatado (+1 contador)
 * 6. Si responde mal → pierde oportunidad
 * 7. Al acabar el tiempo → pantalla de resultados
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    // Estado del juego
    private var selectedCharacter = Character.CHEETO
    private var currentLevel = 1
    private var lives = 3
    private var catsRescued = 0
    private var score = 0
    private var currentQuestion: MathQuestion? = null
    private var currentCat: GameCat? = null
    private var catCounter = 0
    private var isQuestionVisible = false

    private lateinit var timer: CountDownTimer
    private var timeLeft = 60L

    // Gatos en pantalla
    private val activeCatViews = mutableMapOf<Int, ImageView>()
    private val activeCats = mutableMapOf<Int, GameCat>()
    private var catSpawnTimer: CountDownTimer? = null

    private val level get() = LEVELS.getOrNull(currentLevel - 1) ?: LEVELS.last()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar datos del Intent
        val charName = intent.getStringExtra("character") ?: Character.CHEETO.name
        selectedCharacter = Character.valueOf(charName)
        currentLevel = intent.getIntExtra("level", 1)
        timeLeft = level.timeSeconds.toLong()

        updateHUD()
        drawHearts()
        startGame()
        setupQuestionButtons()
    }

    // ── HUD ───────────────────────────────────────────────────────────────

    private fun updateHUD() {
        binding.tvCats.text = "Gatos: $catsRescued/${level.goalCats}"
        binding.tvLevel.text = "NIVEL $currentLevel"
        updateTimerDisplay(timeLeft)
    }

    private fun updateTimerDisplay(seconds: Long) {
        val m = seconds / 60
        val s = seconds % 60
        binding.tvTimer.text = "Tiempo: %02d:%02d".format(m, s)
        binding.tvTimer.setTextColor(
            if (seconds <= 10) android.graphics.Color.RED
            else android.graphics.Color.parseColor("#FFD700")
        )
    }

    private fun drawHearts() {
        binding.livesContainer.removeAllViews()
        for (i in 1..3) {
            val iv = ImageView(this).apply {
                setImageResource(if (i <= lives) R.drawable.ic_heart_full else R.drawable.ic_heart_empty)
                layoutParams = FrameLayout.LayoutParams(36, 36).also {
                    it.marginEnd = 4
                }
            }
            binding.livesContainer.addView(iv)
        }
    }

    // ── Game loop ─────────────────────────────────────────────────────────

    private fun startGame() {
        // Countdown timer
        timer = object : CountDownTimer(timeLeft * 1000, 1000) {
            override fun onTick(ms: Long) {
                timeLeft = ms / 1000
                updateTimerDisplay(timeLeft)
            }
            override fun onFinish() { endGame() }
        }.start()

        // Spawn cats every 2.5 seconds
        catSpawnTimer = object : CountDownTimer(60_000, 2500) {
            override fun onTick(ms: Long) {
                if (!isQuestionVisible) spawnCat()
            }
            override fun onFinish() {}
        }.start()
    }

    private fun spawnCat() {
        if (activeCats.size >= 6) return  // max 6 gatos en pantalla

        val id = catCounter++
        val character = Character.values().random()
        val hasCollar = Random.nextBoolean()

        // Random position in game area (avoid HUD)
        val areaW = binding.gameArea.width.takeIf { it > 0 } ?: 900
        val areaH = binding.gameArea.height.takeIf { it > 0 } ?: 420
        val x = Random.nextInt(50, areaW - 150).toFloat()
        val y = Random.nextInt(areaH / 3, areaH - 120).toFloat()

        val cat = GameCat(id, character, hasCollar, x, y)
        activeCats[id] = cat

        val drawable = if (hasCollar) character.drawableCollarRes else character.drawableRes
        val iv = ImageView(this).apply {
            setImageResource(drawable)
            layoutParams = FrameLayout.LayoutParams(120, 120).also {
                it.leftMargin = x.toInt()
                it.topMargin = y.toInt()
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener { onCatClicked(id) }
        }

        // Bounce animation
        val bounce = android.view.animation.ScaleAnimation(
            0.8f, 1.0f, 0.8f, 1.0f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 300 }
        iv.startAnimation(bounce)

        binding.gameArea.addView(iv)
        activeCatViews[id] = iv

        // Auto-remove cat after 5 seconds if not tapped
        iv.postDelayed({ removeCat(id) }, 5000)
    }

    private fun onCatClicked(catId: Int) {
        if (isQuestionVisible) return
        val cat = activeCats[catId] ?: return

        if (cat.hasCollar) {
            showCollarNotification()
            removeCat(catId)
            return
        }

        // Show question
        currentCat = cat
        val question = QuestionGenerator.generate(level.operation)
        currentQuestion = question
        showQuestion(question)
    }

    private fun removeCat(id: Int) {
        activeCatViews[id]?.let { binding.gameArea.removeView(it) }
        activeCatViews.remove(id)
        activeCats.remove(id)
    }

    // ── Question display ──────────────────────────────────────────────────

    private fun showQuestion(question: MathQuestion) {
        isQuestionVisible = true
        binding.questionOverlay.visibility = View.VISIBLE

        binding.tvFeedback.visibility = View.GONE
        binding.etAnswer.visibility = View.GONE
        binding.gridOptions.visibility = View.GONE
        binding.layoutTrueFalse.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE

        when (question.type) {
            QuestionType.OPEN -> {
                binding.tvQuestion.text = question.display
                binding.etAnswer.visibility = View.VISIBLE
                binding.etAnswer.text?.clear()
                binding.btnConfirm.visibility = View.VISIBLE
                binding.tvQuestionTitle.text = "¡DESAFÍO MATEMÁTICO!"
            }
            QuestionType.MULTIPLE_CHOICE -> {
                binding.tvQuestion.text = question.display
                binding.gridOptions.visibility = View.VISIBLE
                binding.btnOpt1.text = "a. ${question.options[0]}"
                binding.btnOpt2.text = "b. ${question.options[1]}"
                binding.btnOpt3.text = "c. ${question.options[2]}"
                binding.btnOpt4.text = "d. ${question.options[3]}"
                binding.tvQuestionTitle.text = "OPCIÓN MÚLTIPLE"
            }
            QuestionType.TRUE_FALSE -> {
                binding.tvQuestion.text = question.tfStatement
                binding.layoutTrueFalse.visibility = View.VISIBLE
                binding.tvQuestionTitle.text = "VERDADERO O FALSO"
            }
        }
    }

    private fun setupQuestionButtons() {
        // Open answer confirm
        binding.btnConfirm.setOnClickListener {
            val ans = binding.etAnswer.text?.toString()?.toIntOrNull()
            if (ans == null) {
                Toast.makeText(this, "Ingresa un número", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            checkAnswer(ans == currentQuestion?.correctAnswer)
        }

        // Multiple choice
        listOf(binding.btnOpt1, binding.btnOpt2, binding.btnOpt3, binding.btnOpt4)
            .forEachIndexed { i, btn ->
                btn.setOnClickListener {
                    val chosen = currentQuestion?.options?.getOrNull(i)
                    checkAnswer(chosen == currentQuestion?.correctAnswer)
                }
            }

        // True/False
        binding.btnTrue.setOnClickListener {
            checkAnswer(currentQuestion?.tfCorrectIsTrue == true)
        }
        binding.btnFalse.setOnClickListener {
            checkAnswer(currentQuestion?.tfCorrectIsTrue == false)
        }
    }

    private fun checkAnswer(isCorrect: Boolean) {
        // Hide inputs
        binding.etAnswer.visibility = View.GONE
        binding.gridOptions.visibility = View.GONE
        binding.layoutTrueFalse.visibility = View.GONE
        binding.btnConfirm.visibility = View.GONE

        if (isCorrect) {
            catsRescued++
            score += 100
            binding.tvFeedback.text = "¡CORRECTO! 🐾"
            binding.tvFeedback.setTextColor(android.graphics.Color.parseColor("#00AA00"))
            currentCat?.let { removeCat(it.id) }
        } else {
            binding.tvFeedback.text = "¡INCORRECTO!"
            binding.tvFeedback.setTextColor(android.graphics.Color.RED)
            currentCat?.let { removeCat(it.id) }
        }

        binding.tvFeedback.visibility = View.VISIBLE
        updateHUD()

        // Check win condition
        if (catsRescued >= level.goalCats) {
            binding.root.postDelayed({ winLevel() }, 800)
            return
        }

        // Hide question after 1 second
        binding.root.postDelayed({
            binding.questionOverlay.visibility = View.GONE
            binding.tvFeedback.visibility = View.GONE
            isQuestionVisible = false
            currentCat = null
            currentQuestion = null
        }, 1000)
    }

    private fun showCollarNotification() {
        binding.collarOverlay.visibility = View.VISIBLE
        binding.root.postDelayed({
            binding.collarOverlay.visibility = View.GONE
        }, 1500)
    }

    // ── End game ──────────────────────────────────────────────────────────

    private fun winLevel() {
        stopTimers()
        goToResult(true)
    }

    private fun endGame() {
        stopTimers()
        goToResult(catsRescued >= level.goalCats)
    }

    private fun stopTimers() {
        timer.cancel()
        catSpawnTimer?.cancel()
    }

    private fun goToResult(won: Boolean) {
        startActivity(Intent(this, ResultActivity::class.java).apply {
            putExtra("won", won)
            putExtra("catsRescued", catsRescued)
            putExtra("score", score)
            putExtra("level", currentLevel)
            putExtra("character", selectedCharacter.name)
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
        catSpawnTimer?.cancel()
    }
}
