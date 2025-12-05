package compiler

import compiler.backend.Compiler

import java.io.File

private val code = """

begin
    print(2 + 3 * 4); 
end

""".trimIndent()

fun main() {
    //    FlatLightLaf.setup()
    //    JFrame.setDefaultLookAndFeelDecorated(true)

    val ams = Compiler().compile(code)

    extracted(ams)

    //invokeLater { CodeEditor() }
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