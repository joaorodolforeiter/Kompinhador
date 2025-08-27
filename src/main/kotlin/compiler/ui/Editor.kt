package compiler.ui

import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.undo.UndoManager

class Editor : JTextArea() {
    private val undoManager = UndoManager()

    init {
        border = NumberedBorder()

        document.addUndoableEditListener {
            undoManager.addEdit(it.edit)
        }

        registerShortcut(KeyEvent.VK_Z, "undo", ::undo)
        registerShortcut(KeyEvent.VK_Y, "redo", ::redo)
    }

    private fun registerShortcut(key: Int, actionName: String, action: () -> Unit) {
        val keyStroke = KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK)
        inputMap.put(keyStroke, actionName)
        actionMap.put(actionName, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) = action()
        })
    }

    private fun undo() {
        if (undoManager.canUndo()) undoManager.undo()
    }

    private fun redo() {
        if (undoManager.canRedo()) undoManager.redo()
    }
}
