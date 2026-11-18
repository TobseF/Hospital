/*
 * ©2005 by Tobias Fritz - Kotlin/Android port of the original Java AWT game.
 */
package de.tfr.hospital

import android.graphics.Bitmap
import android.graphics.Color
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.pow

/** The four on screen buttons. */
enum class Button { LEFT, RIGHT, OK, MENU }

/**
 * Port of `Game.java` and `Methods.java`.
 *
 * Game flow, flight paths, timings, colors and all drawing coordinates are taken over from the
 * original as they were. Like the AWT version, the game draws directly onto a never cleared
 * screen (here: [bitmap]), which [GameView] scales up to the display.
 *
 * The keyboard controls (S, P, N, ESC, 1-3, H) have been replaced by a menu, which only needs
 * LEFT, RIGHT, OK and MENU.
 */
class HospitalGame(
    private val onFrame: () -> Unit,
    private val onQuit: () -> Unit,
) : Runnable {

    companion object {
        /** Size of the visible AWT canvas inside the original window (see the README gif). */
        const val WIDTH = 620
        const val HEIGHT = 412

        private const val MENU_START = 0
        private const val MENU_NEW = 1
        private const val MENU_DIFFICULTY = 2
        private const val MENU_QUIT = 3
        private const val MENU_ITEMS = 4
    }

    val bitmap: Bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888).apply {
        eraseColor(Color.BLACK)
    }
    private val g = Gfx(bitmap)

    /** Guards the game state and the bitmap. Shared by the game thread and the UI thread. */
    val lock = ReentrantLock()
    private val wakeUp = lock.newCondition()
    private var thread: Thread? = null

    // Variable Values______________________________________
    private var position = 300 // Position of the Paddle
    private var y = 250
    private val xstart = 100
    private var x = xstart
    private var xcanvas = 111 // For the conversion of the X-values of the functions to Canvas
    private var ycanvas = 111 // For the conversion of the Y-values of the functions to Canvas
    private var paddleLength = 50 // Length of the trampoline between the people
    //______________________________________________________

    // Game Flow____________
    private var speed = 10
    private var lives = 490
    private var points = 0
    private var selection = 1 // which flight path should be selected
    private var help = true
    private var pause = true
    private var playing = false
    private var reallyEnd = false
    @Volatile
    private var game = true
    private var gameover = false

    //draw_hospital____________________
    private var yPerson = 20
    private var xPerson = 5
    private var posXSmoke = 76
    private var posYSmoke = 18
    //_____________________________________

    // Functions___________
    private val start = 200 // Initial X-value for the flight paths
    private val stop = 600 // Maximum X-value for the flight paths
    private var a0 = 250.0
    private var a1 = -0.8
    private var a2 = 0.0
    private var a3 = 0.0
    private var a4 = 0.0
    //_____________________

    // Animation______________
    private var gameoverBlink = 0
    private var counter = 0 // People wave their arms
    //________________________

    // Touch menu (replaces the keyboard shortcuts)
    private var menuIndex = MENU_START
    private var quitYes = false
    private var gameoverBeforeQuit = false
    private var pauseShown = false

    fun start() {
        lock.withLock { drawHelp() }
        onFrame()
        thread = Thread(this, "Hospital").also { it.start() }
    }

    fun stop() {
        game = false
        thread?.interrupt()
        thread = null
    }

    // ______________________________________________________________ Game flow

    override fun run() { // Game flow that calls all methods
        try {
            while (game) {
                while (game && playing) {
                    while (game && y >= 5) {
                        if (!lock.withLock { pause }) {
                            frame { moveFallingPerson() }
                            Thread.sleep(lock.withLock { speed.coerceAtLeast(0) }.toLong())
                            frame { checkBounceAndDraw() }
                        } else {
                            // Pause
                            waitMillis(500)
                        }
                    }
                    frame { patientHitTheGround() }
                }
                frame {
                    // Help is constantly painted as long as the quit dialog is not shown
                    if (!reallyEnd) {
                        drawHelp()
                    }
                    if (gameover && !help) {
                        gameOver()
                    }
                }
                waitMillis(1000)
            }
        } catch (_: InterruptedException) {
            // game stopped
        }
    }

    private fun moveFallingPerson() {
        if (pause) return
        drawShape(x - 7, ycanvas - 3, 16, 31, 0, 0, 0, 3) // Person is deleted
        drawShape(102, 360, 575, 30, 0, 0, 0, 0) // delete pixel errors above the people
        // Polynomial function of 4th degree
        val ydouble = a0 + a1 * x + a2 * x.toDouble().pow(2) + a3 * x.toDouble().pow(3) + a4 * x.toDouble().pow(4)
        y = ydouble.toInt()
        xcanvas = x // Conversion for Canvas
        ycanvas = 380 - y // Conversion for Canvas
        drawPerson(x, ycanvas)
        drawPaddle(position)
        x += 1 // increase x-value by 1
    }

    private fun checkBounceAndDraw() {
        if (pause) return
        if ((y <= 20 && y <= 18 && x >= position && x <= position + paddleLength) || x >= stop || x <= xstart) { // Bounce condition
            points += 1
            selection = ((Math.random() * 3) + 1).toInt()
            when (selection) {
                1 -> path01()
                2 -> path02()
                3 -> path03()
            }
            x = xstart
            y = 300
        }
        drawHospital()
        drawPoints()

        //________________Levels
        if (Math.IEEEremainder(points.toDouble(), 15.0) == 0.0 && points > 0) {
            // every 15 points the game speed is increased and an additional point is awarded
            speed -= 1
            points += 1
        }
    }

    private fun patientHitTheGround() {
        if (y >= 5 || !playing) return
        if (lives <= 550) {
            drawShape(x - 4, 400, 16, 9, 255, 45, 52, 3) // Blood stain
            clearPaddle()
            lives += 30
            drawShape(lives, 8, 18, 30, 255, 45, 52, 3) // Paint over lives
            path02()
            x = xstart
            y = 250
        } else {
            gameover = true
            playing = false
        }
    }

    private inline fun frame(block: () -> Unit) {
        lock.withLock(block)
        onFrame()
    }

    private fun waitMillis(millis: Long) {
        lock.withLock { wakeUp.await(millis, TimeUnit.MILLISECONDS) }
    }

    // ______________________________________________________________ Flight paths

    private fun path01() { // Linear flight: a0:initial height; a1:angle(-0.5 to -1.1)
        a0 = (380 - (Math.random() * 220).toInt()).toDouble() // Random value between 160 and 380
        a1 = -(1.1 - (Math.random() * 0.6).toInt()) // Random value between -0.5 and -1.1
        a2 = 0.0
        a3 = 0.0
        a4 = 0.0
    }

    private fun path02() { // Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = (300 - (Math.random() * 275).toInt()).toDouble() // Random value between 25 and 300
        a1 = 1.0
        a2 = -0.003
        a3 = 0.0
        a4 = -0.0
    }

    private fun path03() { // Arc flight: a0(50to300) initial height a1: width and height ; a2(-0.08to0.003): width
        a0 = (380 - (Math.random() * 240).toInt()).toDouble() // Random value between 140 and 380
        a2 = -0.005 + (Math.random() * 0.003).toInt() // Random value between -0.005 and -0.002
        a1 = 0.0
        a3 = 0.0
        a4 = 0.0
    }

    // ______________________________________________________________ Drawing

    private fun drawShape(posx: Int, posy: Int, posx2: Int, posy2: Int, red: Int, yellow: Int, blue: Int, form: Int) {
        g.color(red, yellow, blue)
        when (form) {
            0 -> g.fillRect(posx, posy, posx2, posy2) // Full rectangle
            1 -> g.drawRect(posx, posy, posx2, posy2) // Rectangle frame
            3 -> g.fillOval(posx, posy, posx2, posy2) // Full oval
            4 -> g.drawOval(posx, posy, posx2, posy2) // Oval frame
        }
    }

    private fun drawLine(startX: Int, startY: Int, endX: Int, endY: Int) {
        g.color = Color.WHITE
        g.drawLine(startX, startY, endX, endY)
    }

    private fun drawText(text: String, number: Int, posx: Int, posy: Int, red: Int, yellow: Int, blue: Int, fontType: Int, fontSize: Int) {
        g.color(red, yellow, blue)
        g.drawString(if (number >= 1) number.toString() else text, posx, posy, fontType, fontSize)
    }

    /** Draws [text] so that it ends at [right]. */
    private fun drawTextRight(text: String, right: Int, posy: Int, red: Int, yellow: Int, blue: Int) {
        val width = g.stringWidth(text, 1, 12)
        drawText(text, 0, right - width, posy, red, yellow, blue, 1, 12)
    }

    private fun drawPerson(posx: Int, posy: Int) {
        g.color = Color.WHITE
        counter += 1 // Let people wave their arms
        if (counter >= 0) {
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16) // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16) // left arm down
            g.color = Color.BLACK
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9) // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9) // left arm up
            g.color = Color.WHITE
        }
        if (counter >= 20) {
            g.drawLine(posx + 3, posy + 13, posx - 4, posy + 9) // right arm up
            g.drawLine(posx + 3, posy + 13, posx + 10, posy + 9) // left arm up
            g.color = Color.BLACK
            g.drawLine(posx + 2, posy + 13, posx - 4, posy + 16) // right arm down
            g.drawLine(posx + 2, posy + 13, posx + 10, posy + 16) // left arm down
            g.color = Color.WHITE
            if (counter >= 40) {
                counter = 0
            }
        }
        g.drawOval(posx, posy, 7, 7) // Head
        g.drawOval(posx - 1, posy - 1, 8, 8) // Head
        g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16) // Neck
        g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24) // right leg
        g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24) // left leg
    }

    private fun drawPaddle(position: Int) { // Draw paddle
        var posx = position - 10
        val posy = 380
        var temp = 1
        drawShape(position, 388, paddleLength, 5, 0, 0, 109, 0) // Trampoline
        g.color = Color.WHITE
        while (temp >= 0) {
            g.drawOval(posx, posy, 7, 7) // Head
            g.drawOval(posx - 1, posy - 1, 8, 8) // Head
            if (temp == 0) {
                g.drawLine(posx + 3, posy + 12, posx - 4, posy + 9) // left arm up
                g.drawLine(posx + 3, posy + 14, posx - 4, posy + 11) // left arm up
            } else {
                g.drawLine(posx + 3, posy + 12, posx + 10, posy + 9) // right arm up
                g.drawLine(posx + 3, posy + 14, posx + 10, posy + 11) // right arm up
            }
            g.drawLine(posx + 3, posy + 6, posx + 3, posy + 16) // Neck
            g.drawLine(posx + 3, posy + 16, posx + 8, posy + 24) // right leg
            g.drawLine(posx + 3, posy + 16, posx - 2, posy + 24) // left leg
            temp -= 1
            posx = position + paddleLength + 4
        }
        g.clipped(position, 388, paddleLength, 5) {
            g.color = Color.BLUE
            g.fillOval(position - 20, 385, 90, 10)
            g.color = Color.BLACK
            g.fillOval(position - 20, 360, 90, 31)
        }
    }

    private fun clearPaddle() { // Paint over paddle with black
        g.color = Color.BLACK
        g.fillRect(55, 376, 575, 30)
    }

    private fun drawHospital() { // Hospital
        if (x <= 105) {
            drawShape(0, 15, 100, 405, 102, 102, 102, 0) // House
            drawShape(50, 364, 50, 12, 214, 237, 245, 0) // Hospital sign
            drawShape(50, 376, 50, 60, 0, 0, 0, 0) // Door
            drawShape(5, 20, 16, 22, 0, 206, 255, 0) // Window
            drawText("Hospital", 0, 52, 375, 255, 0, 0, 0, 12)
        }
        drawShape(74, 16, 14, 14, 167, 167, 167, 3) // Smoke
        // draw_smoke_________________________
        posXSmoke += 2
        posYSmoke -= 1
        val lengthSmoke = (Math.random() * 16).toInt() + 4
        val heightSmoke = (Math.random() * 16).toInt() + 4
        val smokeColor = (Math.random() * 240).toInt() + 15
        drawShape(posXSmoke, posYSmoke, lengthSmoke, heightSmoke, smokeColor, smokeColor, smokeColor, 3)
        if (posYSmoke <= 0) {
            posXSmoke = 52
            posYSmoke = 22
        }
        //___________________________________
        var xf = 5
        var yf = 20
        xPerson += 1
        if (xPerson >= 80) {
            yPerson += 40
            xPerson = 0
        }
        if (yPerson >= 320) {
            xPerson = 5
            yPerson = 20
        }
        for (i in 7 downTo 0) { // Draw windows
            drawPatient(xf, yf)
            for (i2 in 1 downTo 0) {
                xf += 30
                drawPatient(xf, yf)
            }
            yf += 40
            xf = 5
        }
        drawShape(posXSmoke, posYSmoke, lengthSmoke, heightSmoke, smokeColor, smokeColor, smokeColor, 3)
    }

    private fun drawPatient(xf: Int, yf: Int) {
        drawShape(xf, yf, 16, 22, 0, 206, 255, 0)
        g.clipped(xf, yf, 16, 22) { // Area in which the shadow is painted is formed
            g.color = Color.BLACK // default foreground of the AWT canvas
            g.drawOval(xPerson - 1, yPerson + 4, 8, 8) // Head
            g.drawLine(xPerson + 3, yPerson + 11, xPerson + 3, yPerson + 21) // Neck
            g.drawLine(xPerson + 3, yPerson + 21, xPerson + 8, yPerson + 29) // Right leg
            g.drawLine(xPerson + 3, yPerson + 21, xPerson - 2, yPerson + 29) // Left leg
            g.drawLine(xPerson + 3, yPerson + 17, xPerson + 10, yPerson + 14) // Right arm up
            g.drawLine(xPerson + 3, yPerson + 19, xPerson + 10, yPerson + 16) // Right arm up
        }
    }

    private fun drawPoints() { // Score board
        drawShape(540, 42, 60, 16, 174, 186, 204, 0)
        drawText("", points, 545, 55, 255, 0, 0, 1, 14)
        drawShape(500, 5, 112, 62, 250, 250, 250, 1)
        drawShape(500, 5, 111, 61, 250, 250, 250, 1)
        drawShape(500, 5, 110, 60, 255, 255, 255, 1)
        drawPerson(525, 10)
        drawPerson(555, 10)
        drawPerson(585, 10)
        drawText("L:", 0, 506, 28, 255, 255, 255, 1, 12)
        drawText("P:", 0, 506, 53, 255, 255, 255, 1, 12)
    }

    /** The help screen of the original, turned into a menu which is navigated with LEFT, RIGHT and OK. */
    private fun drawHelp() {
        val posx = 220
        val posy = 120
        val right = posx + 147
        drawShape(posx - 5, posy - 15, 158, 158, 0, 0, 0, 0) // paints over help field with black
        if (help) {
            val title = "MENU"
            drawText(title, 0, posx + 74 - g.stringWidth(title, 1, 12) / 2, posy, 255, 255, 255, 1, 12)
            for (item in 0 until MENU_ITEMS) {
                val rowY = posy + 18 + item * 14
                val selected = item == menuIndex
                val c = if (selected) 0 else 255
                if (selected) {
                    drawShape(posx - 2, rowY - 11, 152, 14, 255, 255, 255, 0)
                }
                drawText(menuLabel(item), 0, posx, rowY, c, c, c, 1, 12)
                if (item == MENU_DIFFICULTY) {
                    drawTextRight("< ${difficulty()} >", right, rowY, c, c, c)
                }
            }
            drawText("Select", 0, posx, posy + 84, 255, 255, 255, 1, 12)
            drawTextRight("<  >", right, posy + 84, 255, 255, 255)
            drawText("Confirm / Pause", 0, posx, posy + 97, 255, 255, 255, 1, 12)
            drawTextRight("OK", right, posy + 97, 255, 255, 255)
            drawText("Show Menu", 0, posx, posy + 110, 255, 255, 255, 1, 12)
            drawTextRight("MENU", right, posy + 110, 255, 255, 255)
            drawText("Goal: Save as many lives", 0, posx, posy + 125, 255, 255, 255, 1, 12)
            drawText("          as possible.", 0, posx, posy + 138, 255, 255, 255, 1, 12)
            drawShape(posx - 4, posy - 14, 155, 156, 255, 255, 255, 1)
        }
    }

    private fun menuLabel(item: Int) = when (item) {
        MENU_START -> if (playing && !gameover) "Continue" else "Start Game"
        MENU_NEW -> "Restart Game"
        MENU_DIFFICULTY -> "Difficulty"
        else -> "End Game"
    }

    /** Difficulty 1-3 sets the size of the catching mesh, like the keys 1-3 did. */
    private fun difficulty() = when (paddleLength) {
        40 -> 1
        50 -> 2
        else -> 3
    }

    private fun gameOver() { // Game Over Text
        drawText("Game Over", 0, 175, 200, 255, 0, 0, 1, 50)
        if (gameoverBlink == 0) {
            drawText("Restart Game", 0, 220, 350, 255, 0, 0, 1, 12)
            drawTextRight("OK", 375, 350, 255, 0, 0)
            gameoverBlink += 1
        } else {
            drawText("Restart Game", 0, 220, 350, 255, 255, 255, 1, 12)
            drawTextRight("OK", 375, 350, 255, 255, 255)
            gameoverBlink -= 1
        }
        drawText("Menu", 0, 220, 362, 255, 255, 255, 1, 12)
        drawTextRight("MENU", 375, 362, 255, 255, 255)
    }

    private fun clearGameOver() {
        drawShape(170, 155, 280, 60, 0, 0, 0, 0)
        drawShape(215, 336, 165, 30, 0, 0, 0, 0)
    }

    private fun drawQuitDialog() {
        drawShape(241, 162, 114, 50, 0, 0, 0, 0)
        drawShape(241, 162, 114, 50, 255, 255, 255, 1)
        drawText("Really Quit", 0, 265, 180, 255, 255, 255, 1, 12)
        drawText("Yes", 0, 270, 198, 255, 255, 255, 1, 12)
        drawText("No", 0, 310, 198, 255, 255, 255, 1, 12)
        // The selected answer is underlined
        if (quitYes) {
            drawLine(270, 200, 270 + g.stringWidth("Yes", 1, 12) - 1, 200)
        } else {
            drawLine(310, 200, 310 + g.stringWidth("No", 1, 12) - 1, 200)
        }
    }

    // ______________________________________________________________ Input

    /** Handles a press of one of the four on screen buttons. */
    fun press(button: Button) = input {
        when {
            reallyEnd -> quitDialogInput(button)
            help -> menuInput(button)
            else -> gameInput(button)
        }
    }

    /** The Android back button: closes dialogs and the menu, or asks to quit. */
    fun back() = input {
        when {
            reallyEnd -> cancelQuit()
            help && playing && !gameover -> continueGame()
            help -> openQuitDialog()
            else -> openMenu()
        }
    }

    /** Pauses the game when the app is sent to the background. */
    fun onAppPaused() = input {
        if (playing && !gameover && !pause && !reallyEnd) {
            openMenu()
        }
    }

    /** The original keyboard controls, for devices with a hardware keyboard. */
    fun originalKey(key: Char) = input {
        when (key.lowercaseChar()) {
            '1' -> setDifficulty(40)
            '2' -> setDifficulty(50)
            '3' -> setDifficulty(60)
            's' -> if (!reallyEnd) {
                if (gameover) newGame() else continueGame()
            }
            'p' -> if (!reallyEnd && !help && playing && !gameover) togglePause()
            'n' -> if (reallyEnd) cancelQuit() else newGame()
            'y' -> if (reallyEnd) quit()
            'h' -> if (!reallyEnd) {
                if (help && playing && !gameover) continueGame() else if (!help) openMenu()
            }
            '\u001b' -> if (!reallyEnd) openQuitDialog()
        }
    }

    private inline fun input(block: () -> Unit) {
        lock.withLock {
            if (!game) return
            block()
            wakeUp.signalAll()
        }
        onFrame()
    }

    private fun gameInput(button: Button) {
        when (button) {
            Button.LEFT -> {
                // if the trampoline goes too far to the left, or pause is pressed, it stays in place
                if (!(position <= start - 100 || pause || gameover)) {
                    position -= paddleLength
                    clearPaddle()
                    drawPaddle(position)
                }
            }
            Button.RIGHT -> {
                // if the trampoline goes too far to the right, or pause is pressed, it stays in place
                if (!(position >= stop - 50 || pause || gameover)) {
                    position += paddleLength
                    clearPaddle()
                    drawPaddle(position)
                }
            }
            Button.OK -> when {
                gameover -> newGame()
                playing -> togglePause()
                else -> openMenu()
            }
            Button.MENU -> openMenu()
        }
    }

    private fun menuInput(button: Button) {
        when (button) {
            Button.LEFT -> {
                menuIndex = (menuIndex + MENU_ITEMS - 1) % MENU_ITEMS
                drawHelp()
            }
            Button.RIGHT -> {
                menuIndex = (menuIndex + 1) % MENU_ITEMS
                drawHelp()
            }
            Button.OK -> when (menuIndex) {
                MENU_START -> if (gameover) newGame() else continueGame()
                MENU_NEW -> newGame()
                MENU_DIFFICULTY -> {
                    setDifficulty(
                        when (paddleLength) {
                            40 -> 50
                            50 -> 60
                            else -> 40
                        }
                    )
                }
                MENU_QUIT -> openQuitDialog()
            }
            Button.MENU -> if (playing && !gameover) continueGame()
        }
    }

    private fun quitDialogInput(button: Button) {
        when (button) {
            Button.LEFT, Button.RIGHT -> {
                quitYes = !quitYes
                drawQuitDialog()
            }
            Button.OK -> if (quitYes) quit() else cancelQuit()
            Button.MENU -> cancelQuit()
        }
    }

    private fun setDifficulty(length: Int) {
        paddleLength = length
        if (help) drawHelp()
    }

    /** Former key S: closes the menu and starts or continues the game. */
    private fun continueGame() {
        help = false
        drawHelp()
        pause = false
        playing = true
    }

    /** Former key P. */
    private fun togglePause() {
        if (pause) {
            pause = false
            pauseShown = false
            drawText("Pause", 0, 260, 200, 0, 0, 0, 1, 25) // clear pause
        } else {
            pause = true
            pauseShown = true
            drawText("Pause", 0, 260, 200, 255, 255, 255, 1, 25) // draw pause
        }
    }

    /** Former key H: shows the menu and pauses the game. */
    private fun openMenu() {
        if (pauseShown) {
            drawText("Pause", 0, 260, 200, 0, 0, 0, 1, 25) // clear pause
            pauseShown = false
        }
        if (gameover) {
            clearGameOver()
        }
        pause = true
        help = true
        menuIndex = MENU_START
        drawHelp()
    }

    /** Former key N: everything is reset to default values. */
    private fun newGame() {
        help = false
        pause = false
        pauseShown = false
        playing = true
        drawShape(0, 0, 650, 500, 0, 0, 0, 0)
        points = 0
        lives = 490
        speed = 10
        gameover = false
        selection = 1
        x = xstart
        y = 300
    }

    /** Former key ESC. */
    private fun openQuitDialog() {
        pause = true
        reallyEnd = true
        gameoverBeforeQuit = gameover
        gameover = false // so that GameOver text is not overwritten
        quitYes = false
        drawQuitDialog()
    }

    private fun cancelQuit() {
        drawShape(238, 158, 118, 56, 0, 0, 0, 0)
        reallyEnd = false
        gameover = gameoverBeforeQuit
        if (help) {
            drawHelp()
        } else {
            pause = false
        }
    }

    private fun quit() {
        game = false
        onQuit()
    }
}
