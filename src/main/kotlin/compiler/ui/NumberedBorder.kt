package compiler.ui

import java.awt.*
import javax.swing.JTextArea
import javax.swing.border.AbstractBorder

internal class NumberedBorder : AbstractBorder() {
    private val characterHeight = 8
    private val characterWidth = 7
    private val borderColor = Color(164, 164, 164)
    private var lineHeight = 0

    override fun paintBorder(c: Component?, g: Graphics, x: Int, y: Int, width: Int, height: Int) {
        val textArea = c as? JTextArea ?: return
        val metrics = g.getFontMetrics(textArea.font)
        lineHeight = metrics.height

        val oldColor = g.color
        g.color = borderColor

        val lineLeft = calculateLeft(height) + 10
        val visibleLines = textArea.height / lineHeight

        for (i in 0 until visibleLines) {
            val lineNumber = (i + 1).toString()
            val px = lineLeft - (characterWidth * lineNumber.length) - 2
            val py = lineHeight * i + 14
            g.drawString(lineNumber, px, py)
        }

        g.drawLine(lineLeft, 0, lineLeft, height)
        g.color = oldColor
    }

    override fun getBorderInsets(c: Component): Insets {
        val left = calculateLeft(c.height) + 13
        return Insets(1, left, 1, 1)
    }

    private fun calculateLeft(height: Int): Int {
        val rows = ((height.toDouble() / lineHeight) + 0.5).toInt()
        val digits = rows.toString().length
        return characterHeight * digits
    }
}