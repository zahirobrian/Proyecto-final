package com.ipn.miaumission.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.ipn.miaumission.databinding.ActivitySplashBinding
import com.ipn.miaumission.ui.character.CharacterSelectActivity

/**
 * Pantalla de inicio estilo arcade.
 * Muestra el título con el texto "PRESS TO START" parpadeando.
 * Al tocar inicia la selección de personaje.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animación de parpadeo en "PRESS TO START"
        val blink = AlphaAnimation(1f, 0f).apply {
            duration = 600
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        binding.tvPressStart.startAnimation(blink)

        // Cualquier toque en la pantalla avanza
        binding.root.setOnClickListener { goToCharacterSelect() }
        binding.tvPressStart.setOnClickListener { goToCharacterSelect() }
    }

    private fun goToCharacterSelect() {
        startActivity(Intent(this, CharacterSelectActivity::class.java))
        finish()
    }
}
