package compiler.ui

import java.awt.Color
import java.awt.Font
import javax.swing.JTextArea

class Console : JTextArea() {
    init {
        isEditable = false;
        font = Font(Font.MONOSPACED, Font.PLAIN, 14);
        background = Color.BLACK;
        foreground = Color.WHITE
    }

    fun clear() {
        this.text = "";
    }

    fun appendLine(text: String) {
        if (this.document.length > 0) {
            this.append("\n")
        }
        this.append(text)
    }
}