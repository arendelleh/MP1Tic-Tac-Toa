package com.example.mp1tic_tac_toa

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.cos
import kotlin.math.sin

enum class PastelTheme(
    val title: String, val subtitle: String, val icon: String,
    val background: Int, val accent: Int, val soft: Int, val ink: Int, val second: Int
) {
    FLORAL("Floral", "A little good moves, good growth.", "✿",
        Color.rgb(255, 247, 250), Color.rgb(159, 65, 104), Color.rgb(249, 220, 232),
        Color.rgb(75, 49, 69), Color.rgb(224, 238, 221)),
    FRUITS("Fruits", "Fresh picks. Sweet little victories.", "꩜",
        Color.rgb(255, 250, 239), Color.rgb(151, 76, 39), Color.rgb(255, 224, 194),
        Color.rgb(83, 61, 45), Color.rgb(232, 238, 191)),
    SCENERY("Scenery", "Slow down. Make your next destination.", "☁",
        Color.rgb(243, 248, 255), Color.rgb(66, 97, 142), Color.rgb(215, 230, 250),
        Color.rgb(48, 65, 86), Color.rgb(221, 234, 221));

    companion object {
        fun load(context: Context): PastelTheme {
            val name = context.getSharedPreferences("design", Context.MODE_PRIVATE)
                .getString("theme", FLORAL.name)
            return entries.firstOrNull { it.name == name } ?: FLORAL
        }
    }

    fun save(context: Context) {
        context.getSharedPreferences("design", Context.MODE_PRIVATE).edit()
            .putString("theme", name).apply()
    }
}

class PastelUi(val activity: AppCompatActivity, val theme: PastelTheme) {
    val colors = PastelColors.resolve(activity, theme)
    fun dp(value: Int) = (value * activity.resources.displayMetrics.density).toInt()

    fun shape(color: Int, radius: Int = 24, stroke: Int? = null) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()
        stroke?.let { setStroke(dp(1), it) }
    }

    fun text(value: String, size: Float = 16f, bold: Boolean = false) = TextView(activity).apply {
        text = value
        textSize = size
        setTextColor(colors.ink)
        gravity = Gravity.CENTER
        if (bold) typeface = Typeface.create("sans-serif-rounded", Typeface.BOLD)
    }

    fun column() = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }

    fun add(parent: LinearLayout, view: View, top: Int = 0, height: Int = -2) {
        parent.addView(view, LinearLayout.LayoutParams(-1, if (height >= 0) dp(height) else height).apply {
            topMargin = dp(top)
        })
    }

    fun page(): LinearLayout {
        val scroll = ScrollView(activity).apply {
            setBackgroundColor(colors.background)
            isFillViewport = true
            clipToPadding = false
        }
        val outer = column().apply {
            gravity = Gravity.CENTER
            setPadding(dp(22), dp(20), dp(22), dp(24))
        }
        val content = column()
        val appearance = button("Appearance · ${Appearance.load(activity).label} ▾") {}
        appearance.contentDescription = "Appearance: ${Appearance.load(activity).label}. Choose light, dark, or system."
        appearance.setOnClickListener {
            PopupMenu(activity, appearance).apply {
                Appearance.entries.forEachIndexed { index, option ->
                    menu.add(0, index, index, option.label).apply {
                        isCheckable = true
                        isChecked = option == Appearance.load(activity)
                    }
                }
                menu.setGroupCheckable(0, true, true)
                setOnMenuItemClickListener { item ->
                    val selected = Appearance.entries[item.itemId]
                    selected.select(activity)
                    // System and an explicit mode may resolve to the same palette,
                    // in which case AppCompat does not need to recreate the screen.
                    appearance.text = "Appearance · ${selected.label} ▾"
                    appearance.contentDescription = "Appearance: ${selected.label}. Choose light, dark, or system."
                    true
                }
                show()
            }
        }
        add(content, appearance)
        add(content, View(activity), height = 16)
        outer.addView(content, LinearLayout.LayoutParams(
            minOf(activity.resources.displayMetrics.widthPixels - dp(44), dp(420)), -2
        ))
        scroll.addView(outer)
        ViewCompat.setOnApplyWindowInsetsListener(scroll) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        activity.setContentView(scroll)
        WindowCompat.getInsetsController(activity.window, scroll).apply {
            isAppearanceLightStatusBars = !colors.dark
            isAppearanceLightNavigationBars = !colors.dark
        }
        ViewCompat.requestApplyInsets(scroll)
        return content
    }

    fun button(label: String, primary: Boolean = false, action: () -> Unit) = Button(activity).apply {
        text = label
        isAllCaps = false
        textSize = 15f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        setTextColor(if (primary) colors.onAccent else colors.ink)
        background = shape(if (primary) colors.accent else colors.surface, 18,
            if (primary) null else colors.soft)
        minHeight = dp(52)
        setPadding(dp(12), dp(8), dp(12), dp(8))
        setOnClickListener { action() }
    }

    fun themes(parent: LinearLayout, compact: Boolean = false, selected: (PastelTheme) -> Unit) {
        add(parent, text("CHOOSE YOUR LITTLE WORLD", 11f, true), if (compact) 16 else 24)
        val row = LinearLayout(activity)
        PastelTheme.entries.forEachIndexed { index, option ->
            val choice = button("${option.icon}\n${option.title}") { selected(option) }.apply {
                textSize = 13f
                background = shape(if (option == theme) colors.soft else colors.surface, 16,
                    if (option == theme) colors.accent else colors.soft)
                isSelected = option == theme
                contentDescription = "${option.title} theme${if (isSelected) ", selected" else ""}"
            }
            row.addView(choice, LinearLayout.LayoutParams(0, dp(if (compact) 60 else 68), 1f).apply {
                if (index > 0) leftMargin = dp(8)
            })
        }
        add(parent, row, 10)
    }

    fun hero(parent: LinearLayout, title: String, subtitle: String) {
        add(parent, GardenArt(activity, theme), height = 96)
        add(parent, text(title, 30f, true), 8)
        add(parent, text(subtitle, 14f), 8)
    }
}

/** Lightweight vector doodles; no bitmap downloads or extra graphics dependencies. */
class GardenArt(context: Context, private val theme: PastelTheme) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colors = PastelColors.resolve(context, theme)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val scale = minOf(width / 360f, height / 100f)
        canvas.translate((width - 360f * scale) / 2f, (height - 100f * scale) / 2f)
        canvas.scale(scale, scale)
        fun oval(color: Int, x: Float, y: Float, rx: Float, ry: Float) {
            paint.color = color
            canvas.drawOval(x - rx, y - ry, x + rx, y + ry, paint)
        }
        oval(colors.soft, 180f, 55f, 137f, 43f)
        when (theme) {
            PastelTheme.FLORAL -> {
                listOf(108f to 48f, 180f to 37f, 251f to 55f).forEachIndexed { i, (x, y) ->
                    paint.color = Color.rgb(132, 165, 132)
                    paint.strokeWidth = 3f
                    canvas.drawLine(x, y, x - 4, 91f, paint)
                    oval(theme.second, x + 10, 77f, 13f, 6f)
                    for (petal in 0..5) {
                        val angle = petal * Math.PI / 3
                        oval(if (i == 1) Color.WHITE else Color.rgb(237, 171, 196),
                            x + cos(angle).toFloat() * 15, y + sin(angle).toFloat() * 15, 11f, 11f)
                    }
                    oval(Color.rgb(241, 199, 100), x, y, 9f, 9f)
                }
            }
            PastelTheme.FRUITS -> {
                oval(Color.rgb(239, 159, 133), 110f, 58f, 24f, 26f)
                oval(Color.rgb(247, 180, 148), 130f, 58f, 22f, 26f)
                oval(Color.rgb(138, 170, 111), 129f, 29f, 14f, 6f)
                oval(Color.rgb(246, 213, 103), 205f, 57f, 30f, 24f)
                oval(Color.rgb(138, 170, 111), 219f, 29f, 12f, 6f)
                oval(Color.rgb(206, 112, 130), 265f, 62f, 13f, 15f)
                oval(Color.rgb(227, 138, 149), 288f, 65f, 13f, 15f)
                paint.color = Color.rgb(113, 149, 99)
                paint.strokeWidth = 3f
                canvas.drawLine(265f, 48f, 278f, 29f, paint)
                canvas.drawLine(288f, 51f, 278f, 29f, paint)
            }
            PastelTheme.SCENERY -> {
                oval(Color.rgb(247, 216, 132), 239f, 35f, 19f, 19f)
                oval(Color.WHITE, 120f, 35f, 25f, 10f)
                oval(Color.WHITE, 134f, 29f, 17f, 13f)
                oval(Color.rgb(177, 201, 189), 157f, 80f, 77f, 21f)
                oval(Color.rgb(142, 179, 167), 227f, 85f, 68f, 16f)
                oval(Color.rgb(202, 220, 242), 180f, 98f, 103f, 10f)
            }
        }
        canvas.restore()
    }
}
