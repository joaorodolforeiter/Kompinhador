package compiler.ui

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

object Clipboard {

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    fun copy(text: String) {
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }

    fun paste(): String {
        return clipboard.getData(clipboard.availableDataFlavors[0]) as? String ?: ""
    }

}