package compiler

import com.formdev.flatlaf.FlatLightLaf
import compiler.backend.Compiler

import compiler.ui.CodeEditor
import javax.swing.*
import javax.swing.SwingUtilities.invokeLater

fun main() {
//    FlatLightLaf.setup()
//    JFrame.setDefaultLookAndFeelDecorated(true)

    print(Compiler().compile("""
        begin 
         int lado; 
         read ("digite um valor para lado: ", lado);
         print ("o valor digitado foi: ", lado); 
        end
    """.trimIndent()))

    //invokeLater { CodeEditor() }
}
