package com.example.mp1tic_tac_toa

class TicTacToeGame {
    var board = Array(3) { Array(3) { "" } }
    var currentPlayer = "X"
    var isGameOver = false

    fun makeMove(row: Int, col: Int): Boolean {
        if (row !in 0..2 || col !in 0..2 || isGameOver || board[row][col].isNotEmpty()) return false
        board[row][col] = currentPlayer
        isGameOver = checkWinner() != null
        if (!isGameOver) currentPlayer = if (currentPlayer == "X") "O" else "X"
        return true
    }

    fun winningCells(): List<Int> = lines.firstOrNull { line ->
        val mark = board[line[0] / 3][line[0] % 3]
        mark.isNotEmpty() && line.all { board[it / 3][it % 3] == mark }
    } ?: emptyList()

    fun checkWinner(): String? {
        val line = winningCells()
        if (line.isNotEmpty()) return board[line[0] / 3][line[0] % 3]
        return if (board.all { row -> row.all { it.isNotEmpty() } }) "Draw" else null
    }

    // Search on a copy so choosing a move never changes the visible board.
    fun computerMove(): Int? {
        if (isGameOver || currentPlayer != "O" || checkWinner() != null) return null
        val cells = board.flatMap { it.toList() }.toMutableList()
        fun score(turn: String, depth: Int): Int {
            for (line in lines) {
                if (cells[line[0]].isNotEmpty() && line.all { cells[it] == cells[line[0]] }) {
                    return if (cells[line[0]] == "O") 10 - depth else depth - 10
                }
            }
            if (cells.none { it.isEmpty() }) return 0
            val scores = cells.indices.filter { cells[it].isEmpty() }.map { index ->
                cells[index] = turn
                val result = score(if (turn == "O") "X" else "O", depth + 1)
                cells[index] = ""
                result
            }
            return if (turn == "O") scores.max() else scores.min()
        }
        return listOf(4, 0, 2, 6, 8, 1, 3, 5, 7)
            .filter { cells[it].isEmpty() }
            .maxByOrNull { index ->
                cells[index] = "O"
                val result = score("X", 0)
                cells[index] = ""
                result
            }
    }

    fun reset() {
        board = Array(3) { Array(3) { "" } }
        currentPlayer = "X"
        isGameOver = false
    }

    companion object {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
    }
}
