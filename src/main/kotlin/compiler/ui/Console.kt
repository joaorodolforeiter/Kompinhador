package compiler.ui

import java.awt.Color
import javax.swing.JTextArea

class Console : JTextArea() {
    init {
        isEditable = false;
        background = Color.BLACK;
        foreground = Color.WHITE
    }
}