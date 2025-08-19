package compiler.ui

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

class CodeEditor : JFrame("Compilador") {

    private var file: File? = null
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    private val editor = JTextArea().apply { border = NumberedBorder() }
    private val console = JTextArea().apply { isEditable = false; background = Color.BLACK; foreground = Color.WHITE }
    private val messageArea = JLabel()
    private val fileChooser = JFileChooser().apply { dialogTitle = "Selecione um arquivo" }

    private val menu = JMenuBar().apply {
        menuItem("Novo", "/icons/new.png", KeyEvent.VK_N, ::newFile)
        menuItem("Abrir", "/icons/new.png", KeyEvent.VK_O, ::openFile)
        menuItem("Salvar", "/icons/save.png", KeyEvent.VK_S, ::saveFile)
        menuItem("Copiar", "/icons/copy.png", KeyEvent.VK_C, ::copy)
        menuItem("Colar", "/icons/paste.png", KeyEvent.VK_V, ::paste)
        menuItem("Recortar", "/icons/cut.png", KeyEvent.VK_X, ::cut)
        menuItem("Compilar", "/icons/compile.png", KeyEvent.VK_F7, ::compile)
        menuItem("Equipe", "/icons/team.png", KeyEvent.VK_F1, ::showTeam)
    }

    init {
        size = 1500 by 800
        isResizable = false
        defaultCloseOperation = EXIT_ON_CLOSE
        jMenuBar = menu
        layout = BorderLayout()

        add(
            JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
                topComponent = scrollPanel(editor).apply { minimumSize = 1500 by 300; preferredSize = 1500 by 400 }
                bottomComponent = scrollPanel(console)
            }
        )

        add(messageArea, BorderLayout.SOUTH)

        isVisible = true
    }

    private fun newFile() {
        editor.text = ""
        console.text = ""
        messageArea.text = ""
        file = null
    }

    private fun openFile() {
        when (fileChooser.showSaveDialog(this@CodeEditor)) {
            JFileChooser.APPROVE_OPTION -> {
                val selectedFile = fileChooser.selectedFile
                editor.text = selectedFile.readText(Charsets.UTF_8)

                messageArea.text = selectedFile.absolutePath
            }

            JFileChooser.CANCEL_OPTION -> {
                messageArea.text = "Operação cancelada pelo usuário."
            }

            JFileChooser.ERROR_OPTION -> {
                messageArea.text = "Ocorreu um erro ao selecionar o arquivo."
            }
        }
    }

    private fun saveFile() {
        if (file == null) {
            when (fileChooser.showSaveDialog(this@CodeEditor)) {
                JFileChooser.APPROVE_OPTION -> {
                    file = fileChooser.selectedFile
                    messageArea.text = "Arquivo salvo em: ${file?.absolutePath}"
                }

                JFileChooser.CANCEL_OPTION -> {
                    messageArea.text = "Operação cancelada pelo usuário."
                }

                JFileChooser.ERROR_OPTION -> {
                    messageArea.text = "Ocorreu um erro ao salvar o arquivo."
                }
            }
        }

        file?.writeText(editor.text)
    }

    private fun copy() {
        clipboard.setContents(StringSelection(editor.selectedText ?: editor.text), null)
    }

    private fun paste() {
        val text = clipboard.getData(DataFlavor.stringFlavor) as String
        editor.insert(text, editor.caretPosition)
    }

    private fun cut() {
        if (editor.selectedText == null) return

        clipboard.setContents(StringSelection(editor.selectedText), null)
        editor.replaceSelection("")
    }

    private fun compile() {
        messageArea.text = "Compilação de programas ainda não foi implementada"
    }

    private fun showTeam() {
        messageArea.text = "Equipe: Lucas Will, João Rodolfo Reiter, Lucas Eduardo \uD83D\uDE0E"
    }

    private infix fun Int.by(y: Int) = Dimension(this, y)

    private fun Container.menuItem(
        text: String,
        iconPath: String,
        shortCut: Int,
        onClick: () -> Unit
    ): JMenuItem {
        val item = JMenuItem(text).apply {
            icon = loadIcon(iconPath)
            accelerator = KeyStroke.getKeyStroke(
                shortCut,
                if (shortCut in KeyEvent.VK_A..KeyEvent.VK_Z) KeyEvent.CTRL_DOWN_MASK else 0
            )

            addActionListener { onClick() }
        }
        add(item)
        return item
    }

    private fun scrollPanel(component: Component): JScrollPane = JScrollPane(
        component,
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    )

    private fun loadIcon(path: String): ImageIcon {
        val resource = object {}.javaClass.getResource(path)
            ?: throw IllegalArgumentException("Resource not found: $path")

        val original = ImageIcon(resource).image
        val scaled: Image = original.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
        return ImageIcon(scaled)
    }

}
