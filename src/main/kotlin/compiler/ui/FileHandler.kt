package compiler.ui

import java.io.File
import java.nio.file.Files
import java.nio.file.Files.writeString
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.Timer
import kotlin.io.writeText

class FileHandler(
    private val onFileChanged: (String) -> Unit,
    private val onStatusChanged: (String) -> Unit
) {
    private val fileChooser = JFileChooser().apply { dialogTitle = "Selecione um arquivo" }

    private var currentFile: File? = null
    private var assemblyFile: File? = null

    private var fileModificationDate: Long = 0

    init {
        Timer(1000) { listenForFileModifications() }
    }

    fun newFile() {
        currentFile = null
        fileModificationDate = 0
        onFileChanged("")
        onStatusChanged("")
    }

    fun openFile() {
        val selectedFile = getFile()

        if (selectedFile != null) {
            if (!selectedFile.exists()) {
                selectedFile.createNewFile();
            }
            currentFile = selectedFile
            fileModificationDate = selectedFile.lastModified()

            onFileChanged(selectedFile.readText(Charsets.UTF_8))
            onStatusChanged(selectedFile.absolutePath)
        } else {
            onStatusChanged("Operação cancelada pelo usuário.")
        }
    }

    fun saveFile(content: String) {
        if (currentFile == null) openFile()

        synchronized(this) {
            currentFile?.writeText(content)
            onFileChanged(content)
            fileModificationDate = currentFile?.lastModified() ?: 0
        }
    }

    fun createAssemblyFile(content: String) {
        currentFile?.let { file ->
            val fileName = "${file.name.substringBeforeLast(".")}.il"
            val parentPath = file.toPath().parent.toAbsolutePath()

            writeString(parentPath.resolve(fileName), content, Charsets.UTF_8)
        }
    }

    private fun getFile(): File? {
        return when (fileChooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> fileChooser.selectedFile
            else -> null
        }
    }

    private fun listenForFileModifications() {
        while (true) {
            var lastModified: Long
            synchronized(this) {
                lastModified = currentFile?.lastModified() ?: 0
            }

            if (lastModified > fileModificationDate) {
                val option = JOptionPane.showConfirmDialog(
                    null, "O arquivo foi sobrescrito por outra aplicação. Deseja carregar as alterações?"
                )
                if (option == JOptionPane.YES_OPTION) {
                    currentFile?.let { file ->
                        onFileChanged(file.readText())
                    }
                }
                fileModificationDate = currentFile?.lastModified() ?: 0
            }
            Thread.sleep(100)
        }
    }
}
