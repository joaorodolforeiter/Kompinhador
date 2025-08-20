package compiler.ui

import java.awt.*
import java.awt.event.KeyEvent
import javax.swing.*

class CodeEditor : JFrame("Compilador") {

    private val editor = JTextArea().apply { border = NumberedBorder() }
    private val console = JTextArea().apply { isEditable = false; background = Color.BLACK; foreground = Color.WHITE }
    private val messageArea = JLabel()

    init {
        size = 1500 by 800
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(
            Toolbar(editor, console, messageArea),
            BorderLayout.NORTH
        )

        add(
            JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
                topComponent = scrollPanel(editor).apply { minimumSize = 1500 by 300; preferredSize = 1500 by 400 }
                bottomComponent = scrollPanel(console)
            }
        )

        add(messageArea, BorderLayout.SOUTH)

        isVisible = true
    }

    private infix fun Int.by(y: Int) = Dimension(this, y)

    private fun scrollPanel(component: Component): JScrollPane = JScrollPane(
        component,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    )
}
