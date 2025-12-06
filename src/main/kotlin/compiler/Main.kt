package compiler

import com.formdev.flatlaf.FlatLightLaf
import compiler.backend.Compiler
import compiler.ui.CodeEditor

import java.io.File
import javax.swing.JFrame
import javax.swing.SwingUtilities.invokeLater

private val code = """
begin
    print((true) and (1));
end
""".trimIndent()

fun main() {
//        FlatLightLaf.setup()
//        JFrame.setDefaultLookAndFeelDecorated(true)

    val ams = Compiler().compile(code)

    extracted(ams)

//    invokeLater { CodeEditor() }
}

private fun extracted(ams: String) {
    print(ams)

    // Write IL code to file
    val ilFile = File("output.il")
    ilFile.writeText(ams)

    // Compile with ilasm
    val ilasmProcess = ProcessBuilder("ilasm", "/exe", "/output=output.exe", ilFile.absolutePath)
        .redirectErrorStream(true)
        .start()

    val ilasmOutput = ilasmProcess.inputStream.bufferedReader().readText()
    ilasmProcess.waitFor()

    if (ilasmProcess.exitValue() == 0) {
        println(ilasmOutput)

        println("\nRunning the executable:")
        val runProcess = ProcessBuilder("mono", "output.exe")
            .redirectErrorStream(true)
            .start()

        val runOutput = runProcess.inputStream.bufferedReader().readText()
        println(runOutput)
        runProcess.waitFor()
    } else {
        println("Compilation failed:")
        println(ilasmOutput)
    }
}