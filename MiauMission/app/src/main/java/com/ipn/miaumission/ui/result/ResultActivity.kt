package com.ipn.miaumission.ui.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ipn.miaumission.databinding.ActivityResultBinding
import com.ipn.miaumission.model.Character
import com.ipn.miaumission.model.LEVELS
import com.ipn.miaumission.ui.character.CharacterSelectActivity
import com.ipn.miaumission.ui.game.GameActivity

/**
 * Pantalla de resultado: nivel completado o game over.
 * Muestra gatos rescatados, puntuación y opciones de continuar o volver al menú.
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val won = intent.getBooleanExtra("won", false)
        val catsRescued = intent.getIntExtra("catsRescued", 0)
        val score = intent.getIntExtra("score", 0)
        val level = intent.getIntExtra("level", 1)
        val charName = intent.getStringExtra("character") ?: Character.CHEETO.name
        val character = Character.valueOf(charName)

        // UI
        binding.tvResultTitle.text = if (won) "¡NIVEL COMPLETADO!" else "GAME OVER"
        binding.tvResultTitle.setTextColor(
            if (won) android.graphics.Color.parseColor("#FFA500")
            else android.graphics.Color.RED
        )
        binding.imgResultCat.setImageResource(character.drawableRes)
        binding.tvCatsRescued.text = "Gatos rescatados: $catsRescued"
        binding.tvScore.text = "Puntuación: $score"

        // Next level button
        val nextLevel = level + 1
        if (won && nextLevel <= LEVELS.size) {
            binding.btnNextLevel.text = "SIGUIENTE"
            binding.btnNextLevel.setOnClickListener {
                startActivity(Intent(this, GameActivity::class.java).apply {
                    putExtra("character", charName)
                    putExtra("level", nextLevel)
                })
                finish()
            }
        } else {
            binding.btnNextLevel.text = "JUGAR DE NUEVO"
            binding.btnNextLevel.setOnClickListener {
                startActivity(Intent(this, GameActivity::class.java).apply {
                    putExtra("character", charName)
                    putExtra("level", 1)
                })
                finish()
            }
        }

        binding.btnMenu.setOnClickListener {
            startActivity(Intent(this, CharacterSelectActivity::class.java))
            finish()
        }
    }
}
