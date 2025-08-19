package compiler

import com.formdev.flatlaf.FlatIntelliJLaf

import compiler.ui.CodeEditor
import javax.swing.*

fun main() {
    FlatIntelliJLaf.setup()
    JFrame.setDefaultLookAndFeelDecorated(true)

    CodeEditor()
}

