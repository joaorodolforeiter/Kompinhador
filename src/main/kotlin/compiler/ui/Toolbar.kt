package compiler.ui

import java.awt.Dimension
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent.*
import javax.swing.*

class Toolbar : JToolBar("Toolbar") {

    init {
        isFloatable = false
        preferredSize = Dimension(size.width, 70)
    }

    fun button(
        text: String,
        iconPath: String,
        shortCut: KeyStroke,
        onClick: () -> Unit
    ) {
        val button = JButton(text).apply {
            toolTipText = "$text (${shortCut.formatedString()})"
            icon = loadIcon(iconPath)

            verticalTextPosition = BOTTOM
            horizontalTextPosition = CENTER

            preferredSize = Dimension(80, 70)
            minimumSize = Dimension(80, 70)
            maximumSize = Dimension(80, 70)

            addActionListener { onClick() }

            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(shortCut, text)
            actionMap.put(text, object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) = onClick()
            })
        }

        add(button)
    }

    private fun KeyStroke.formatedString(): String {
        val parts = mutableListOf<String>()

        if (modifiers and CTRL_DOWN_MASK != 0) parts.add("Ctrl")

        parts.add(getKeyText(keyCode))

        return parts.joinToString(" + ")
    }

    private fun loadIcon(path: String): ImageIcon {
        val resource = object {}.javaClass.getResource(path)
            ?: throw IllegalArgumentException("Resource not found: $path")

        val original = ImageIcon(resource).image

        return original.getScaledInstance(24, 24, Image.SCALE_SMOOTH).let(::ImageIcon)
    }

}