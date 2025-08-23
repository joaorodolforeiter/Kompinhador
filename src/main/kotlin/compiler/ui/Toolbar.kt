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
import kotlin.concurrent.thread

class Toolbar(
    private val editor: JTextArea,
    private val console: JTextArea,
    private val messageArea: JLabel
) : JToolBar("Toolbar") {

    private var file: File? = null
    private var fileModificationDate: Long = 0
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

        thread { listenForFileModifications() }
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
        console.text = ""
        messageArea.text = ""
        file = null
        fileModificationDate = 0
    }

    private fun openFile() {
        when (fileChooser.showSaveDialog(this@Toolbar)) {
            JFileChooser.APPROVE_OPTION -> {
                val selectedFile = fileChooser.selectedFile
                editor.text = selectedFile.readText(Charsets.UTF_8)
                messageArea.text = selectedFile.absolutePath
                file = selectedFile
                fileModificationDate = selectedFile.lastModified()
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
            when (fileChooser.showSaveDialog(this@Toolbar)) {
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
        synchronized(this) {
            file?.writeText(editor.text)
            fileModificationDate = file?.lastModified() ?: 0
        }
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

    private fun listenForFileModifications() {
        while (true) {
            var lastModified: Long
            synchronized(this) {
                lastModified = file?.lastModified() ?: 0
            }

            if (lastModified > fileModificationDate) {
                val option = JOptionPane.showConfirmDialog(editor, "O arquivo foi sobrescrito por outra aplicação. Deseja carregar as alterações?")
                if (option == JOptionPane.YES_OPTION) {
                    editor.text = file?.readText()
                }
                fileModificationDate = file?.lastModified() ?: 0
            }
            Thread.sleep(100)
        }
    }
}