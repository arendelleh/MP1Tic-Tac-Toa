package com.example.mp1tic_tac_toa

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate

enum class Appearance(val label: String, val nightMode: Int) {
    SYSTEM("System", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    LIGHT("Light", AppCompatDelegate.MODE_NIGHT_NO),
    DARK("Dark", AppCompatDelegate.MODE_NIGHT_YES);

    fun select(context: Context) {
        context.getSharedPreferences("design", Context.MODE_PRIVATE).edit()
            .putString("appearance", name).apply()
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    companion object {
        fun load(context: Context): Appearance {
            val saved = context.getSharedPreferences("design", Context.MODE_PRIVATE)
                .getString("appearance", SYSTEM.name)
            return entries.firstOrNull { it.name == saved } ?: SYSTEM
        }
    }
}

class TicTacToeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(Appearance.load(this).nightMode)
    }
}

data class PastelColors(
    val background: Int, val surface: Int, val accent: Int, val soft: Int,
    val ink: Int, val second: Int, val onAccent: Int, val dark: Boolean
) {
    companion object {
        fun resolve(context: Context, theme: PastelTheme): PastelColors {
            val dark = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            if (!dark) return PastelColors(theme.background, Color.WHITE, theme.accent,
                theme.soft, theme.ink, theme.second, Color.WHITE, false)
            val colors = when (theme) {
                PastelTheme.FLORAL -> listOf("#211B24", "#302633", "#F0AFCE", "#483143", "#F9EAF3", "#344D40")
                PastelTheme.FRUITS -> listOf("#241F1B", "#332B24", "#F5BD8D", "#49382B", "#FFF0DF", "#414B30")
                PastelTheme.SCENERY -> listOf("#19212C", "#242F3D", "#AECBF4", "#30445E", "#EAF2FF", "#304D48")
            }.map(Color::parseColor)
            return PastelColors(colors[0], colors[1], colors[2], colors[3], colors[4],
                colors[5], colors[0], true)
        }
    }
}
