package compiler.ui

import java.awt.Dimension
import javax.swing.JLabel

class StatusBar : JLabel() {
    init {
        preferredSize = Dimension(0, 25)
    }

    fun clear() {
        text = ""
    }

    fun setMessage(msg: String) {
        text = msg
    }
}