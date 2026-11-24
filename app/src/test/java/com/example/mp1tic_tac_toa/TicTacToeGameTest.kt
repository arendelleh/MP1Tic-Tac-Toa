package com.example.mp1tic_tac_toa

import org.junit.Assert.*
import org.junit.Test

class TicTacToeGameTest {
    @Test fun computerTakesWinningMove() {
        val game = TicTacToeGame()
        game.board = arrayOf(arrayOf("O", "O", ""), arrayOf("X", "X", ""), arrayOf("X", "", ""))
        game.currentPlayer = "O"
        assertEquals(2, game.computerMove())
        assertTrue(game.makeMove(0, 2))
        assertEquals("O", game.checkWinner())
        assertTrue(game.isGameOver)
        assertFalse(game.makeMove(2, 2))
    }

    @Test fun computerBlocksHumanWinWithoutMutatingBoard() {
        val game = TicTacToeGame()
        game.board = arrayOf(arrayOf("X", "X", ""), arrayOf("", "O", ""), arrayOf("", "", ""))
        game.currentPlayer = "O"
        val before = game.board.map { it.toList() }
        assertEquals(2, game.computerMove())
        assertEquals(before, game.board.map { it.toList() })
    }

    @Test fun movesAlternateAndOccupiedCellsAreRejected() {
        val game = TicTacToeGame()
        assertNull(game.computerMove())
        assertTrue(game.makeMove(0, 0))
        assertEquals("O", game.currentPlayer)
        assertFalse(game.makeMove(0, 0))
        val move = game.computerMove()!!
        assertTrue(game.makeMove(move / 3, move % 3))
        assertEquals("X", game.currentPlayer)
        assertEquals(2, game.board.flatten().count { it.isNotEmpty() })
        game.reset()
        assertEquals("X", game.currentPlayer)
        assertTrue(game.board.flatten().all { it.isEmpty() })
        assertFalse(game.isGameOver)
    }

    @Test fun computerNeverLosesAcrossAllHumanContinuations() {
        fun explore(game: TicTacToeGame) {
            assertNotEquals("X", game.checkWinner())
            if (game.isGameOver) return
            if (game.currentPlayer == "O") {
                val move = game.computerMove()!!
                game.makeMove(move / 3, move % 3)
                explore(game)
            } else {
                for (index in 0..8) {
                    if (game.board[index / 3][index % 3].isNotEmpty()) continue
                    val branch = TicTacToeGame()
                    branch.board = Array(3) { game.board[it].clone() }
                    branch.makeMove(index / 3, index % 3)
                    explore(branch)
                }
            }
        }
        explore(TicTacToeGame())
    }

    @Test fun drawStopsPlayAndResetClearsResult() {
        val game = TicTacToeGame()
        listOf(0, 1, 2, 4, 3, 5, 7, 6, 8).forEach { assertTrue(game.makeMove(it / 3, it % 3)) }
        assertEquals("Draw", game.checkWinner())
        assertTrue(game.isGameOver)
        assertNull(game.computerMove())
        game.reset()
        assertNull(game.checkWinner())
    }
}
