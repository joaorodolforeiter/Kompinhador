package compiler

import com.formdev.flatlaf.FlatLightLaf
import compiler.ui.CodeEditor

import javax.swing.JFrame
import javax.swing.SwingUtilities.invokeLater

fun main() {
    FlatLightLaf.setup()
    JFrame.setDefaultLookAndFeelDecorated(true)

    invokeLater { CodeEditor() }
}
