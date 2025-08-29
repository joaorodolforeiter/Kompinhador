package compiler

import com.formdev.flatlaf.FlatLightLaf

import compiler.ui.CodeEditor
import javax.swing.*

fun main() {
    FlatLightLaf.setup()
    JFrame.setDefaultLookAndFeelDecorated(true)

    CodeEditor()
}
