package com.ipn.miaumission.ui.character

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ipn.miaumission.R
import com.ipn.miaumission.databinding.ActivityCharacterSelectBinding
import com.ipn.miaumission.model.Character
import com.ipn.miaumission.ui.game.GameActivity

/**
 * Pantalla de selección de personaje.
 * El jugador elige uno de los 5 gatos: Cheeto, Pelusa, Mantecada, Waffle o Lui.
 * La tarjeta seleccionada se resalta en naranja.
 */
class CharacterSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCharacterSelectBinding
    private var selectedCharacter: Character? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCards()

        binding.btnPlay.setOnClickListener {
            val char = selectedCharacter
            if (char == null) {
                Toast.makeText(this, "¡Selecciona un personaje!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("character", char.name)
                putExtra("level", 1)
            })
            finish()
        }
    }

    private fun setupCards() {
        val cards = listOf(
            binding.cardCheeto  to Character.CHEETO,
            binding.cardPelusa  to Character.PELUSA,
            binding.cardMantecada to Character.MANTECADA,
            binding.cardWaffle  to Character.WAFFLE,
            binding.cardLui     to Character.LUI
        )

        cards.forEach { (card, character) ->
            card.setOnClickListener {
                // Deselecciona todas
                cards.forEach { (c, _) -> c.isSelected = false }
                // Selecciona esta
                card.isSelected = true
                selectedCharacter = character
            }
        }
    }
}
