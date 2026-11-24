package com.example.mp1tic_tac_toa

import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameFlowTest {
    private val ids = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8)

    private fun launch(mode: String): ActivityScenario<GameActivity> {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return ActivityScenario.launch(Intent(context, GameActivity::class.java)
            .putExtra("GAME_MODE", mode).putExtra("PLAYER_1", "Erin"))
    }

    @Test fun computerRespondsAndBoardSurvivesRecreation() {
        launch("single").use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<Button>(R.id.btn0).performClick()
                assertTrue(ids.all { !activity.findViewById<Button>(it).isEnabled })
                assertTrue(activity.findViewById<TextView>(R.id.tvStatus).text.contains("thinking"))
            }
            scenario.recreate()
            Thread.sleep(1200)
            scenario.onActivity { activity ->
                val marks = ids.map { activity.findViewById<Button>(it).text.toString() }
                assertEquals(1, marks.count { it == "✕" })
                assertEquals(1, marks.count { it == "○" })
                assertEquals("Erin’s turn", activity.findViewById<TextView>(R.id.tvStatus).text.toString())
            }
        }
    }

    @Test fun resetCancelsPendingComputerMove() {
        launch("single").use { scenario ->
            scenario.onActivity { activity ->
                activity.findViewById<Button>(R.id.btn0).performClick()
                activity.findViewById<Button>(R.id.btnReset).performClick()
            }
            Thread.sleep(1200)
            scenario.onActivity { activity ->
                assertTrue(ids.all {
                    val cell = activity.findViewById<Button>(it)
                    cell.text.isEmpty() && cell.isEnabled
                })
            }
        }
    }

    @Test fun multiplayerWaitsForSecondHuman() {
        launch("multi").use { scenario ->
            scenario.onActivity { it.findViewById<Button>(R.id.btn0).performClick() }
            Thread.sleep(1200)
            scenario.onActivity { activity ->
                assertEquals(1, ids.count { activity.findViewById<Button>(it).text.isNotEmpty() })
                activity.findViewById<Button>(R.id.btn1).performClick()
                assertEquals("○", activity.findViewById<Button>(R.id.btn1).text.toString())
            }
        }
    }
}
