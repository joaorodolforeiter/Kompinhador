package compiler.ui

import java.awt.Image
import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.io.File

class Toolbar(
    private val editor: JTextArea,
    private val console: Console,
    private val statusBar: StatusBar
) : JToolBar("Toolbar") {

    private var file: File? = null
    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private val fileChooser = JFileChooser().apply { dialogTitle = "Selecione um arquivo" }

    init {
        isFloatable = false

        add(createButton(
            "Novo [ctrl + n]",
            "/icons/new.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
            ::newFile
        ))

        add(createButton(
            "Abrir [ctl + o]",
            "/icons/open.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
            ::openFile
        ))

        add(createButton(
            "Salvar [ctl + s]",
            "/icons/save.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
            ::saveFile
        ))

        add(createButton(
            "Copiar [ctl + c]",
            "/icons/copy.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
            ::copy
        ))

        add(createButton(
            "Colar [ctl + v]",
            "/icons/paste.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
            ::paste
        ))

        add(createButton(
            "Recortar [ctl + x]",
            "/icons/cut.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK),
            ::cut
        ))

        add(createButton(
            "Compilar [F7]",
            "/icons/compile.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
            ::compile
        ))

        add(createButton(
            "Equipe [F1]",
            "/icons/team.png",
            KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
            ::showTeam
        ))

    }

    override fun getPreferredSize(): Dimension {
        val size = super.getPreferredSize()
        return Dimension(size.width, 70)
    }

    private fun createButton(
        text: String,
        iconPath: String,
        shortCut: KeyStroke,
        onClick: () -> Unit
    ): JButton {
        return JButton(text).apply {
            toolTipText = text
            icon = loadIcon(iconPath)

            verticalTextPosition = BOTTOM
            horizontalTextPosition = CENTER

            preferredSize = Dimension(120, 70)
            minimumSize = Dimension(120, 70)
            maximumSize = Dimension(120, 70)

            addActionListener { onClick() }

            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(shortCut, text)
            actionMap.put(text, object : AbstractAction() {
                override fun actionPerformed(e: ActionEvent?) {
                    onClick()
                }
            })
        }
    }

    private fun loadIcon(path: String): ImageIcon {
        val resource = object {}.javaClass.getResource(path)
            ?: throw IllegalArgumentException("Resource not found: $path")

        val original = ImageIcon(resource).image
        val scaled: Image = original.getScaledInstance(32, 32, Image.SCALE_SMOOTH)
        return ImageIcon(scaled)
    }

    private fun newFile() {
        editor.text = ""
        console.clear();
        statusBar.clear();
        file = null
    }

    private fun openFile() {
        when (fileChooser.showSaveDialog(this@Toolbar)) {
            JFileChooser.APPROVE_OPTION -> {
                val selectedFile = fileChooser.selectedFile
                editor.text = selectedFile.readText(Charsets.UTF_8)

                statusBar.text = selectedFile.absolutePath
            }

            JFileChooser.CANCEL_OPTION -> {
                statusBar.text = "Operação cancelada pelo usuário."
            }

            JFileChooser.ERROR_OPTION -> {
                statusBar.text = "Ocorreu um erro ao selecionar o arquivo."
            }
        }
    }

    private fun saveFile() {
        if (file == null) {
            when (fileChooser.showSaveDialog(this@Toolbar)) {
                JFileChooser.APPROVE_OPTION -> {
                    file = fileChooser.selectedFile
                    statusBar.text = "Arquivo salvo em: ${file?.absolutePath}"
                }

                JFileChooser.CANCEL_OPTION -> {
                    statusBar.text = "Operação cancelada pelo usuário."
                }

                JFileChooser.ERROR_OPTION -> {
                    statusBar.text = "Ocorreu um erro ao salvar o arquivo."
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
        console.appendLine("Compilação de programas ainda não foi implementada")
    }

    private fun showTeam() {
        console.appendLine("Equipe: Lucas Will, João Rodolfo Reiter, Lucas Eduardo \uD83D\uDE0E")
    }
}