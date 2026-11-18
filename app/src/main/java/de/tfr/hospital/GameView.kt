package de.tfr.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import kotlin.concurrent.withLock
import kotlin.math.min

/**
 * Shows the game screen in the middle and four on screen buttons on the sides:
 *
 * ```
 * [ MENU ]  +-------------------+  [  OK  ]
 *           |                   |
 * [  <   ]  |      Hospital     |  [  >   ]
 *           +-------------------+
 * ```
 */
@SuppressLint("ViewConstructor")
class GameView(context: Context, private val game: HospitalGame) : View(context) {

    private class OnScreenButton(val button: Button) {
        val rect = RectF()
        var pressedBy = -1 // pointer id, -1 when released
        val pressed get() = pressedBy != -1
    }

    private val buttons = Button.entries.map { OnScreenButton(it) }
    private fun button(b: Button) = buttons.first { it.button == b }

    private val screen = Rect()
    private val screenPaint = Paint().apply {
        isFilterBitmap = false // keep the crisp pixels of the original
        isAntiAlias = false
    }
    private val framePaint = Paint().apply {
        color = Color.rgb(102, 102, 102)
        style = Paint.Style.STROKE
    }
    private val buttonFill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val buttonStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val arrow = Path()

    /** Holding LEFT or RIGHT repeats the key, like a held keyboard key did. */
    private val repeat = object : Runnable {
        override fun run() {
            val held = buttons.firstOrNull { it.pressed && (it.button == Button.LEFT || it.button == Button.RIGHT) }
            if (held != null) {
                game.press(held.button)
                postDelayed(this, REPEAT_INTERVAL)
            }
        }
    }

    init {
        isHapticFeedbackEnabled = true
        keepScreenOn = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val ratio = HospitalGame.WIDTH.toFloat() / HospitalGame.HEIGHT
        val minSide = w * 0.15f
        var gameHeight = h.toFloat()
        var gameWidth = gameHeight * ratio
        if ((w - gameWidth) / 2 < minSide) {
            gameWidth = w - 2 * minSide
            gameHeight = gameWidth / ratio
        }
        val left = ((w - gameWidth) / 2).toInt()
        val top = ((h - gameHeight) / 2).toInt()
        screen.set(left, top, left + gameWidth.toInt(), top + gameHeight.toInt())

        val side = left.toFloat()
        val margin = min(side, h.toFloat()) * 0.08f
        val small = h * 0.30f
        // Left: MENU on top, LEFT below. Right: OK on top, RIGHT below.
        button(Button.MENU).rect.set(margin, margin, side - margin, small)
        button(Button.LEFT).rect.set(margin, small + margin, side - margin, h - margin)
        button(Button.OK).rect.set(w - side + margin, margin, w - margin, small)
        button(Button.RIGHT).rect.set(w - side + margin, small + margin, w - margin, h - margin)

        labelPaint.textSize = min(side * 0.22f, small * 0.3f)
        buttonStroke.strokeWidth = resources.displayMetrics.density * 2
        framePaint.strokeWidth = resources.displayMetrics.density
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        game.lock.withLock {
            canvas.drawBitmap(game.bitmap, null, screen, screenPaint)
        }
        canvas.drawRect(screen, framePaint)
        buttons.forEach { drawButton(canvas, it) }
    }

    private fun drawButton(canvas: Canvas, b: OnScreenButton) {
        val r = b.rect
        val corner = min(r.width(), r.height()) * 0.12f
        buttonFill.color = if (b.pressed) Color.rgb(102, 102, 102) else Color.rgb(28, 28, 28)
        buttonStroke.color = if (b.pressed) Color.WHITE else Color.rgb(170, 170, 170)
        canvas.drawRoundRect(r, corner, corner, buttonFill)
        canvas.drawRoundRect(r, corner, corner, buttonStroke)

        when (b.button) {
            Button.LEFT, Button.RIGHT -> {
                val size = min(r.width(), r.height()) * 0.22f
                val dir = if (b.button == Button.LEFT) -1 else 1
                arrow.reset()
                arrow.moveTo(r.centerX() + dir * size, r.centerY())
                arrow.lineTo(r.centerX() - dir * size * 0.6f, r.centerY() - size)
                arrow.lineTo(r.centerX() - dir * size * 0.6f, r.centerY() + size)
                arrow.close()
                labelPaint.style = Paint.Style.FILL
                canvas.drawPath(arrow, labelPaint)
            }
            else -> {
                val label = if (b.button == Button.OK) "OK" else "MENU"
                val baseline = r.centerY() - (labelPaint.descent() + labelPaint.ascent()) / 2
                canvas.drawText(label, r.centerX(), baseline, labelPaint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.actionIndex
                val hit = buttons.firstOrNull { !it.pressed && it.rect.contains(event.getX(index), event.getY(index)) }
                if (hit != null) {
                    hit.pressedBy = event.getPointerId(index)
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    game.press(hit.button)
                    if (hit.button == Button.LEFT || hit.button == Button.RIGHT) {
                        removeCallbacks(repeat)
                        postDelayed(repeat, REPEAT_DELAY)
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                release(event.getPointerId(event.actionIndex))
            }
            MotionEvent.ACTION_CANCEL -> {
                buttons.forEach { it.pressedBy = -1 }
                removeCallbacks(repeat)
                invalidate()
            }
        }
        return true
    }

    private fun release(pointerId: Int) {
        buttons.filter { it.pressedBy == pointerId }.forEach { it.pressedBy = -1 }
        if (buttons.none { it.pressed && (it.button == Button.LEFT || it.button == Button.RIGHT) }) {
            removeCallbacks(repeat)
        }
        invalidate()
    }

    private companion object {
        const val REPEAT_DELAY = 400L
        const val REPEAT_INTERVAL = 120L
    }
}
