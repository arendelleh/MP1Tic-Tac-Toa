package com.example.mp1tic_tac_toa

import android.content.Intent
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        buildScreen()
    }

    private fun buildScreen() {
        val ui = PastelUi(this, PastelTheme.load(this))
        val page = ui.page()
        ui.add(page, ui.text("WELCOME TO THE BRAINY CLUB", 11f, true))
        ui.hero(page, "Tic-Tac-Toe Card", "A stress reliever game.")
        ui.add(page, ui.text("✕   ·   ○   ·   ✕", 36f, true), 26)
        ui.add(page, ui.button("Play with Computer", true) { start("single") }.apply { id = R.id.btnSinglePlayer }, 26)
        ui.add(page, ui.button("Play with a Friend") { start("multi") }.apply { id = R.id.btnMultiplayer }, 12)
        ui.themes(page) { theme -> theme.save(this); buildScreen() }
        ui.add(page, ui.text("Three in a row. A little win to go.", 12f), 24)

        val creditText = ui.text("202410846: Erin Dale T. Gaetos", 12f).apply {
            gravity = Gravity.START
        }
        ui.add(page, creditText, 24)
    }

    private fun start(mode: String) {
        startActivity(Intent(this, PlayerNameActivity::class.java).putExtra("GAME_MODE", mode))
    }
}