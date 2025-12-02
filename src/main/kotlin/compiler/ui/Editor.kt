package compiler.ui

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.undo.UndoManager

class Editor : RSyntaxTextArea() {
    private val undoManager = UndoManager()

    init {
        document.addUndoableEditListener {
            undoManager.addEdit(it.edit)
        }

        text = """
            begin 
             int lado; 
             read ("digite um valor para lado: ", lado);
             print ("o valor digitado foi: ", lado); 
            end
        """.trimIndent()

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
