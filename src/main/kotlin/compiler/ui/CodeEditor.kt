package compiler.ui

import java.awt.*
import javax.swing.*

class CodeEditor : JFrame("Compilador") {
    private val console = Console()
    private val statusBar = StatusBar()
    private val editor = Editor()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        add(Toolbar(editor, console, statusBar), BorderLayout.NORTH)
        add(createSplitPane(), BorderLayout.CENTER)
        add(statusBar, BorderLayout.SOUTH)

        size = Dimension(1500, 800)
        isResizable = false;
        isVisible = true
    }

    private fun createSplitPane(): JSplitPane = JSplitPane(JSplitPane.VERTICAL_SPLIT).apply {
        topComponent = scrollPanel(editor).apply {
            minimumSize = Dimension(1500, 300)
            preferredSize = Dimension(1500, 400)
        }
        bottomComponent = scrollPanel(console)
    }

    private fun scrollPanel(component: Component): JScrollPane =
        JScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
}
