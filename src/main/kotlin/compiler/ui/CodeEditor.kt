package compiler.ui

import java.awt.*
import java.awt.event.InputEvent.CTRL_DOWN_MASK
import java.awt.event.KeyEvent.*
import javax.swing.*
import javax.swing.JSplitPane.VERTICAL_SPLIT
import javax.swing.KeyStroke.getKeyStroke

class CodeEditor : JFrame("Compilador") {

    private val editor = Editor()
    private val console = Console()
    private val statusBar = StatusBar()

    private val fileHandler = FileHandler(
        onFileChanged = {
            editor.text = it
            console.clear()
        },
        onStatusChanged = { statusBar.text = it }
    )

    init {
        size = Dimension(1500, 800)
        layout = BorderLayout()
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        add(Toolbar().apply {
            button("Novo", "/icons/new.png", getKeyStroke(VK_N, CTRL_DOWN_MASK), fileHandler::newFile)
            button("Abrir", "/icons/open.png", getKeyStroke(VK_O, CTRL_DOWN_MASK), fileHandler::openFile)
            button("Salvar", "/icons/save.png", getKeyStroke(VK_S, CTRL_DOWN_MASK), ::saveFile)
            button("Copiar", "/icons/copy.png", getKeyStroke(VK_C, CTRL_DOWN_MASK), ::copy)
            button("Colar", "/icons/paste.png", getKeyStroke(VK_V, CTRL_DOWN_MASK), ::paste)
            button("Recortar", "/icons/cut.png", getKeyStroke(VK_X, CTRL_DOWN_MASK), ::cut)
            button("Compilar", "/icons/compile.png", getKeyStroke(VK_F7, 0), ::compile)
            button("Equipe", "/icons/team.png", getKeyStroke(VK_F1, 0), ::showTeam)
        }, BorderLayout.NORTH)

        add(JSplitPane().apply {
            orientation = VERTICAL_SPLIT
            topComponent = scrollPane(editor).apply { preferredSize = Dimension(1500, 400) }
            bottomComponent = scrollPane(console)
        }, BorderLayout.CENTER)

        add(statusBar, BorderLayout.SOUTH)

        isVisible = true
    }

    private fun saveFile() {
        fileHandler.saveFile(editor.text)
    }

    private fun copy() {
        Clipboard.copy(editor.selectedText ?: editor.text)
    }

    private fun paste() {
        val text = Clipboard.paste()
        editor.insert(text, editor.caretPosition)
    }

    private fun cut() {
        if (editor.selectedText == null) {
            Clipboard.copy(editor.text)
            editor.text = ""
            return
        }

        Clipboard.copy(editor.selectedText)
        editor.replaceSelection("")
    }

    private fun compile() {
        console.appendLine("Compilação de programas ainda não foi implementada")
    }
    
    private fun showTeam() {
        console.appendLine("Equipe: João Rodolfo Reiter, Lucas Eduardo, Lucas Will \uD83D\uDE0E")
    }

    private fun scrollPane(component: Component) = JScrollPane(
        component, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    )

}
