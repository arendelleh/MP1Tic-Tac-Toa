package com.example.mp1tic_tac_toa

import android.content.Intent
import android.content.res.Configuration
import android.widget.Button
import android.widget.EditText
import androidx.core.graphics.ColorUtils
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppearanceTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext

    @Test fun switchingToDarkPreservesGameAndComputerTurn() {
        val original = Appearance.load(context)
        val theme = PastelTheme.load(context)
        try {
            instrumentation.runOnMainSync { Appearance.LIGHT.select(context) }
            ActivityScenario.launch<GameActivity>(
                Intent(context, GameActivity::class.java).putExtra("GAME_MODE", "single")
            ).use { scenario ->
                scenario.onActivity {
                    it.findViewById<Button>(R.id.btn0).performClick()
                    Appearance.DARK.select(it)
                }
                Thread.sleep(1500)
                scenario.onActivity {
                    assertEquals(Appearance.DARK, Appearance.load(it))
                    assertTrue(PastelColors.resolve(it, theme).dark)
                    assertEquals(theme, PastelTheme.load(it))
                    assertEquals("✕", it.findViewById<Button>(R.id.btn0).text.toString())
                    val ids = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8)
                    assertEquals(1, ids.count { id -> it.findViewById<Button>(id).text.toString() == "○" })
                }
            }
        } finally {
            instrumentation.runOnMainSync { original.select(context) }
        }
    }

    @Test fun switchingAppearanceKeepsEnteredNames() {
        val original = Appearance.load(context)
        try {
            instrumentation.runOnMainSync { Appearance.LIGHT.select(context) }
            ActivityScenario.launch<PlayerNameActivity>(
                Intent(context, PlayerNameActivity::class.java).putExtra("GAME_MODE", "multi")
            ).use { scenario ->
                scenario.onActivity {
                    it.findViewById<EditText>(R.id.etPlayer1).setText("Erin")
                    it.findViewById<EditText>(R.id.etPlayer2).setText("Alex")
                    Appearance.DARK.select(it)
                }
                instrumentation.waitForIdleSync()
                scenario.onActivity {
                    assertEquals("Erin", it.findViewById<EditText>(R.id.etPlayer1).text.toString())
                    assertEquals("Alex", it.findViewById<EditText>(R.id.etPlayer2).text.toString())
                }
            }
        } finally {
            instrumentation.runOnMainSync { original.select(context) }
        }
    }

    @Test fun everyThemeHasReadableLightAndDarkPalettes() {
        for (night in listOf(Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_YES)) {
            val configuration = Configuration(context.resources.configuration).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or night
            }
            val configured = context.createConfigurationContext(configuration)
            for (theme in PastelTheme.entries) {
                val palette = PastelColors.resolve(configured, theme)
                assertEquals(night == Configuration.UI_MODE_NIGHT_YES, palette.dark)
                for (surface in listOf(palette.background, palette.surface, palette.soft, palette.second)) {
                    assertTrue("$theme text contrast", ColorUtils.calculateContrast(palette.ink, surface) >= 4.5)
                    assertTrue("$theme mark contrast", ColorUtils.calculateContrast(palette.accent, surface) >= 3.0)
                }
                assertTrue(ColorUtils.calculateContrast(palette.onAccent, palette.accent) >= 4.5)
            }
        }
    }
}
