package com.example.mp1tic_tac_toa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    private val game = TicTacToeGame()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var ui: PastelUi
    private lateinit var status: TextView
    private lateinit var detail: TextView
    private lateinit var buttons: List<Button>
    private var single = false
    private var p1 = "Player 1"
    private var p2 = "Player 2"
    private val computerTurn = Runnable {
        if (single && !game.isGameOver && game.currentPlayer == "O") {
            game.computerMove()?.let { game.makeMove(it / 3, it % 3) }
            render()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        single = intent.getStringExtra("GAME_MODE") == "single"
        p1 = intent.getStringExtra("PLAYER_1") ?: "Player 1"
        p2 = if (single) "Computer" else intent.getStringExtra("PLAYER_2") ?: "Player 2"
        savedInstanceState?.getStringArray("board")?.let { cells ->
            game.board = Array(3) { row -> Array(3) { col -> cells[row * 3 + col] } }
            game.currentPlayer = savedInstanceState.getString("turn") ?: "X"
            game.isGameOver = game.checkWinner() != null
        }
        buildScreen()
    }

    private fun buildScreen() {
        ui = PastelUi(this, PastelTheme.load(this))
        val page = ui.page()
        ui.add(page, ui.text("THE BRAINY CLUB", 11f, true))
        ui.add(page, GardenArt(this, ui.theme), height = 56)
        ui.add(page, ui.text("Tic-Tac-Toe", 26f, true), 6)
        ui.add(page, ui.text(ui.theme.subtitle, 13f), 6)
        val players = ui.text("✕  $p1     ·     ○  $p2", 14f, true).apply {
            setPadding(ui.dp(12), ui.dp(12), ui.dp(12), ui.dp(12))
            background = ui.shape(ui.colors.soft, 18)
        }
        ui.add(page, players, 12)
        val card = ui.column().apply {
            setPadding(ui.dp(14), ui.dp(12), ui.dp(14), ui.dp(12))
            background = ui.shape(ui.colors.surface, 28)
            elevation = ui.dp(3).toFloat()
        }
        status = ui.text("", 21f, true).apply {
            id = R.id.tvStatus
            accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        }
        detail = ui.text("", 13f)
        ui.add(card, status)
        ui.add(card, detail, 6)
        val grid = object : LinearLayout(this) {
            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY))
            }
        }.apply { orientation = LinearLayout.VERTICAL }
        val cells = mutableListOf<Button>()
        val ids = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8)
        repeat(3) { row ->
            val line = LinearLayout(this)
            repeat(3) { col ->
                val index = row * 3 + col
                val button = ui.button("") {
                    if ((!single || game.currentPlayer == "X") && game.makeMove(row, col)) {
                        render()
                        scheduleComputer()
                    }
                }.apply {
                    id = ids[index]
                    textSize = 34f
                    minHeight = 0
                    minimumHeight = 0
                    minWidth = 0
                    minimumWidth = 0
                    gravity = Gravity.CENTER
                }
                line.addView(button, LinearLayout.LayoutParams(0, -1, 1f).apply {
                    setMargins(ui.dp(4), ui.dp(4), ui.dp(4), ui.dp(4))
                })
                cells.add(button)
            }
            grid.addView(line, LinearLayout.LayoutParams(-1, 0, 1f))
        }
        buttons = cells
        ui.add(card, grid, 10)
        ui.add(page, card, 12)
        ui.themes(page, compact = true) { theme ->
            theme.save(this)
            buildScreen()
        }
        val actions = LinearLayout(this)
        actions.addView(ui.button("↻   New round", true) {
            handler.removeCallbacks(computerTurn)
            game.reset()
            render()
        }.apply { id = R.id.btnReset }, LinearLayout.LayoutParams(0, ui.dp(52), 1f))
        actions.addView(ui.button("‹   Players") { finish() },
            LinearLayout.LayoutParams(0, ui.dp(52), 1f).apply { leftMargin = ui.dp(10) })
        ui.add(page, actions, 16)
        render()
    }

    private fun render() {
        val result = game.checkWinner()
        val thinking = single && game.currentPlayer == "O" && !game.isGameOver
        status.text = when (result) {
            "X" -> "$p1 wins! ✨"
            "O" -> "$p2 wins! ✨"
            "Draw" -> "A lovely little tie!"
            else -> if (thinking) "Computer is thinking…" else "${if (game.currentPlayer == "X") p1 else p2}’s turn"
        }
        detail.text = when {
            result != null -> "Another round? A fresh board is waiting."
            thinking -> "Picking a spot — your turn is next."
            else -> "Place your ${if (game.currentPlayer == "X") "✕" else "○"} · three in a row to win"
        }
        val winning = game.winningCells()
        buttons.forEachIndexed { index, button ->
            val mark = game.board[index / 3][index % 3]
            button.text = when (mark) { "X" -> "✕"; "O" -> "○"; else -> "" }
            button.textSize = if (mark == "O") 56f else 34f
            button.isEnabled = mark.isEmpty() && !game.isGameOver && !thinking
            button.setTextColor(if (mark == "O") ui.colors.ink else ui.colors.accent)
            button.background = ui.shape(
                when { index in winning -> ui.colors.second; mark.isNotEmpty() -> ui.colors.soft; else -> ui.colors.background },
                18, if (index in winning) ui.colors.accent else ui.colors.soft
            )
            button.contentDescription = "Row ${index / 3 + 1}, column ${index % 3 + 1}, ${mark.ifEmpty { "empty" }}"
        }
    }

    private fun scheduleComputer() {
        handler.removeCallbacks(computerTurn)
        if (single && game.currentPlayer == "O" && !game.isGameOver) handler.postDelayed(computerTurn, 550)
    }

    override fun onResume() {
        super.onResume()
        scheduleComputer()
    }

    override fun onPause() {
        handler.removeCallbacks(computerTurn)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("board", game.board.flatMap { it.toList() }.toTypedArray())
        outState.putString("turn", game.currentPlayer)
        super.onSaveInstanceState(outState)
    }
}
