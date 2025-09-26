package compiler.ui

import ExprLexer
import ExprParser
import compiler.backend.ErrorListener
import compiler.backend.LexerException
import compiler.backend.Lexer
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.fife.ui.rtextarea.RTextScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.InputEvent.CTRL_DOWN_MASK
import java.awt.event.KeyEvent.*
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JSplitPane.VERTICAL_SPLIT
import javax.swing.KeyStroke.getKeyStroke
import javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
import javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS

class CodeEditor : JFrame("Compilador") {

    private val editor = Editor()
    private val console = Console()
    private val statusBar = StatusBar()

    private val lexer = Lexer()

    private val fileHandler = FileHandler(
        onFileChanged = {
            editor.text = it
            console.clear()
        },
        onStatusChanged = { statusBar.text = it }
    )

    init {
        editor.text = "0_abbc"

        size = Dimension(1500, 800)
        layout = BorderLayout()
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)

        iconImage = ImageIcon(javaClass.getResource("/icons/app.png")).image

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
            topComponent = RTextScrollPane(editor).apply {
                preferredSize = Dimension(1500, 400)
                verticalScrollBarPolicy = VERTICAL_SCROLLBAR_ALWAYS
                horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_ALWAYS
            }
            bottomComponent = JScrollPane(console, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_ALWAYS)
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
        console.clear()

        try {
            val input = ANTLRInputStream(editor.text)  // Input do editor
            val lexer = ExprLexer(input)
            val tokens = CommonTokenStream(lexer)
            tokens.fill()

            val tokenList = tokens.getTokens()
            println(tokenList)

            for (token in tokenList) {
                when (token.type) {
                    ExprLexer.INVALID_IDENTIFIER -> {
                        println("Linha ${token.line}: identificador inválido")  // Ou throw LexerException
                    }
//                    ExprLexer.INVALID_STRING -> {
//                        println("Linha ${token.line}: constante_string inválida")
//                    }
//                    ExprLexer.INVALID_BLOCK_COMMENT -> {
//                        println("Linha ${token.line}: comentário inválido ou não finalizado")
//                    }
//                    ExprLexer.INVALID_SYMBOL -> {
//                        println("Linha ${token.line}: ${token.text} símbolo inválido")
//                    }
                }
            }

//          val parser = ExprParser(tokens)
//          parser.removeErrorListeners()
//          parser.addErrorListener(ErrorListener())

            //val tree = parser.program()
        } catch (e: LexerException) {
            console.appendLine(e.message ?: "Erro léxico desconhecido")
        }
    }

    private fun showTeam() {
        console.appendLine("Equipe: João Rodolfo Reiter, Lucas Eduardo, Lucas Will \uD83D\uDE0E")
    }
}
