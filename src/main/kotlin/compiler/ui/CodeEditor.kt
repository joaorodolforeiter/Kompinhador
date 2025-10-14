package compiler.ui

import ExprLexer
import ExprParser
import compiler.backend.Lexer
import compiler.backend.LexerException
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.ParseCancellationException
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
            val input = CharStreams.fromString(editor.text)
            val lexer = ExprLexer(input)
            val tokens = CommonTokenStream(lexer)

            try {
                val parser = ExprParser(tokens)
                parser.addErrorListener(ParserErrorListener())
                val tree = parser.program()
                print(tree.toStringTree(parser))
            } catch (e: ParseCancellationException) {
                System.err.println("Erro durante o parsing: " + e.message)
            }

        } catch (e: LexerException) {
            console.appendLine(e.message ?: "Erro léxico desconhecido")
        }
    }

    private fun printTokens(tokens: List<Token>) {

        if (tokens.isEmpty()) {
            return console.appendLine("Nenhum token encontrado")
        }

        val maxLength = (tokens.maxOfOrNull { it.text.length } ?: 0).coerceAtLeast(5)

        console.appendLine("-".repeat(maxLength + 35))
        console.appendLine("| %5s | %-20s | %-${maxLength}s |".format("Linha", "Nome", "Texto"))
        console.appendLine("-".repeat(maxLength + 35))

        for (token in tokens) {
            console.appendLine("| %5s | %-20s | %-${maxLength}s |".format(token.line, token.getClass(), token.text))
        }

        console.appendLine("-".repeat(maxLength + 35))
    }

    private fun showTeam() {
        console.appendLine("Equipe: João Rodolfo Reiter, Lucas Eduardo, Lucas Will \uD83D\uDE0E")
    }

    fun Token.getClass(): String = when (ExprLexer.VOCABULARY.getSymbolicName(type) ?: type.toString()) {
        "PR_ADD", "PR_AND", "PR_BEGIN", "PR_BOOL", "PR_COUNT",
        "PR_DELETE", "PR_DO", "PR_ELEMENTOF", "PR_ELSE", "PR_END",
        "PR_FALSE", "PR_FLOAT", "PR_IF", "PR_INT", "PR_LIST",
        "PR_NOT", "PR_OR", "PR_PRINT", "PR_READ", "PR_SIZE",
        "PR_STRING", "PR_TRUE", "PR_UNTIL"
            -> "palavra reservada"

        "PLUS", "MINUS", "TIMES", "DIV", "EQEQ",
        "NEQ", "LT", "GT", "EQ", "ASSIGN",
        "LPAREN", "RPAREN", "SEMI", "COMMA"
            -> "símbolo especial"

        "IDENTIFICADOR" -> "identificador"
        "CINT" -> "constante_int"
        "CFLOAT" -> "constante_float"
        "STRING" -> "constante_string"

        else -> error("Classe não encontrada")
    }
}
