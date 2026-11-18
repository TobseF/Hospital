package de.tfr.hospital

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/**
 * A tiny subset of `java.awt.Graphics`, drawn into a persistent bitmap.
 *
 * The original game never repaints the whole screen. It paints directly onto the AWT canvas and
 * only "erases" by painting black over things. Keeping one bitmap that is never cleared
 * reproduces exactly that look: smoke trails, blood stains and all the little pixel leftovers.
 *
 * Coordinates and sizes follow the AWT conventions:
 * - `fillXxx(x, y, w, h)` covers `w * h` pixels,
 * - `drawXxx(x, y, w, h)` outlines cover `(w + 1) * (h + 1)` pixels,
 * - lines include both end points,
 * - text is drawn at its baseline.
 */
class Gfx(val bitmap: Bitmap) {

    private val canvas = Canvas(bitmap)

    private val paint = Paint().apply {
        isAntiAlias = false
        isFilterBitmap = false
        strokeWidth = 0f // hairline: exactly one pixel, like AWT
    }

    private val sans = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    private val sansBold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    private val mono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    private val serif = Typeface.create(Typeface.SERIF, Typeface.NORMAL)

    var color: Int = Color.WHITE

    fun color(r: Int, g: Int, b: Int) {
        color = Color.rgb(r, g, b)
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int) {
        if (w <= 0 || h <= 0) return
        fill()
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
    }

    fun drawRect(x: Int, y: Int, w: Int, h: Int) {
        if (w < 0 || h < 0) return
        drawLine(x, y, x + w, y)
        drawLine(x, y + h, x + w, y + h)
        drawLine(x, y, x, y + h)
        drawLine(x + w, y, x + w, y + h)
    }

    fun fillOval(x: Int, y: Int, w: Int, h: Int) {
        if (w <= 0 || h <= 0) return
        fill()
        canvas.drawOval(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paint)
    }

    fun drawOval(x: Int, y: Int, w: Int, h: Int) {
        if (w < 0 || h < 0) return
        stroke()
        canvas.drawOval(x + .5f, y + .5f, x + w + .5f, y + h + .5f, paint)
    }

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        stroke()
        canvas.drawLine(x1 + .5f, y1 + .5f, x2 + .5f, y2 + .5f, paint)
        canvas.drawPoint(x1 + .5f, y1 + .5f, paint)
        canvas.drawPoint(x2 + .5f, y2 + .5f, paint)
    }

    /** Draws [text] with its baseline at [y]. Font types follow the original `draw_text`. */
    fun drawString(text: String, x: Int, y: Int, fontType: Int, size: Int) {
        setFont(fontType, size)
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)
    }

    fun stringWidth(text: String, fontType: Int, size: Int): Int {
        setFont(fontType, size)
        return paint.measureText(text).toInt()
    }

    /** Runs [block] with drawing restricted to the given rectangle, like `Graphics.clipRect`. */
    inline fun clipped(x: Int, y: Int, w: Int, h: Int, block: () -> Unit) {
        val save = saveClip(x, y, w, h)
        try {
            block()
        } finally {
            restoreClip(save)
        }
    }

    fun saveClip(x: Int, y: Int, w: Int, h: Int): Int {
        val save = canvas.save()
        canvas.clipRect(x, y, x + w, y + h)
        return save
    }

    fun restoreClip(save: Int) = canvas.restoreToCount(save)

    private fun setFont(fontType: Int, size: Int) {
        paint.style = Paint.Style.FILL
        paint.color = color
        paint.textSize = size.toFloat()
        paint.isFakeBoldText = false
        paint.typeface = when (fontType) {
            0 -> sans
            1 -> sansBold
            2 -> mono
            else -> serif
        }
    }

    private fun fill() {
        paint.style = Paint.Style.FILL
        paint.color = color
    }

    private fun stroke() {
        paint.style = Paint.Style.STROKE
        paint.color = color
    }
}
