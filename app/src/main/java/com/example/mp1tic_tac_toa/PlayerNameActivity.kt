package com.example.mp1tic_tac_toa

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class PlayerNameActivity : AppCompatActivity() {
    private lateinit var player1: EditText
    private lateinit var player2: EditText
    private var displayedTheme: PastelTheme? = null
    private var displayedAppearance: Appearance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen(savedInstanceState?.getString("p1") ?: "", savedInstanceState?.getString("p2") ?: "")
    }

    private fun buildScreen(first: String, second: String) {
        val single = intent.getStringExtra("GAME_MODE") == "single"
        val ui = PastelUi(this, PastelTheme.load(this))
        displayedTheme = ui.theme
        displayedAppearance = Appearance.load(this)
        val page = ui.page()
        ui.add(page, ui.text(if (single) "YOU + COMPUTER" else "A GAME FOR TWO", 11f, true))
        ui.hero(page, "Who’s playing?", if (single) "You make a move. Computer takes the next." else "Two friends, one sweet little rivalry.")
        fun field(label: String, value: String, viewId: Int): EditText {
            ui.add(page, ui.text(label, 14f, true), 22)
            return EditText(this).apply {
                id = viewId
                setText(value)
                hint = label
                contentDescription = label
                textSize = 16f
                setTextColor(ui.colors.ink)
                setHintTextColor(ui.colors.accent)
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                setSingleLine(true)
                filters = arrayOf(android.text.InputFilter.LengthFilter(24))
                setPadding(ui.dp(18), ui.dp(12), ui.dp(18), ui.dp(12))
                background = ui.shape(ui.colors.surface, 18, ui.colors.soft)
                ui.add(page, this, 8, 56)
            }
        }
        player1 = field("Player 1 Name · ✕", first, R.id.etPlayer1)
        player2 = field("Player 2 Name · ○", if (single) "Computer" else second, R.id.etPlayer2)
        player2.isEnabled = !single
        ui.add(page, ui.button("Let’s play  →", true) {
            startActivity(Intent(this, GameActivity::class.java).apply {
                putExtra("PLAYER_1", player1.text.toString().trim().ifEmpty { "Player 1" })
                putExtra("PLAYER_2", if (single) "Computer" else player2.text.toString().trim().ifEmpty { "Player 2" })
                putExtra("GAME_MODE", if (single) "single" else "multi")
            })
        }.apply { id = R.id.btnStartGame }, 28)
        ui.add(page, ui.button("‹   Back to home") { finish() }, 10)
    }

    override fun onResume() {
        super.onResume()
        if (displayedTheme != PastelTheme.load(this) || displayedAppearance != Appearance.load(this)) {
            buildScreen(player1.text.toString(), player2.text.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("p1", player1.text.toString())
        outState.putString("p2", player2.text.toString())
        super.onSaveInstanceState(outState)
    }
}
