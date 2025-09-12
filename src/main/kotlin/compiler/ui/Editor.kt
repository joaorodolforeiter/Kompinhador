package compiler.ui

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.undo.UndoManager

class Editor : RSyntaxTextArea() {
    private val undoManager = UndoManager()

    init {
        text = """
            begin
                // Declaracao de variaveis
                int x;
                float y;
                bool flag;
                string nome;
                list nums;

                // Atribuicoes
                x <- 10;
                y <- 3.14;'
                flag <- true;
                nome <- "João";
                nums <- {1, 2, 3, 4, 5};

                // Condicional
                if x > 5 do
                    print("x é maior que 5");
                else
                    print("x é menor ou igual a 5");
                end

                // Loop com until
                int contador;
                contador <- 0;
                until contador == 5 do
                    print(contador);
                    contador <- contador + 1;
                end

                // Funcoes basicas simuladas
                print("Nome: " + nome);
                print("Numero de elementos na lista: " + count(nums));

            end
        """.trimIndent()

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
