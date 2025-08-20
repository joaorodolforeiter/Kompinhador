package compiler.ui

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.undo.UndoManager
import java.awt.event.InputEvent


class CodeEditor : JFrame("Compilador") {

    private val undoManager = UndoManager()
    private val editor = JTextArea().apply {
        val undoAction = object : AbstractAction("Undo") {
            override fun actionPerformed(e: ActionEvent?) {
                if (undoManager.canUndo()) {
                    undoManager.undo()
                }
            }
        }
        border = NumberedBorder()
        document.addUndoableEditListener {
            undoManager.addEdit(it.edit)
        }

        var map = getInputMap(JComponent.WHEN_FOCUSED)
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoAction")
        actionMap.put("undoAction", undoAction)
    }
    private val console = JTextArea().apply { isEditable = false; background = Color.BLACK; foreground = Color.WHITE }
    private val messageArea = JLabel().apply {
        preferredSize = this@CodeEditor.width by 25
    }

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
