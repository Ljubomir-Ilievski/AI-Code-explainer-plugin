package com.ilievski.ai.plugin.ui

import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class ExplainDialog(private val text: String) : DialogWrapper(true) {

    init {
        init()
        title = "AI Code Explanation"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(BorderLayout())

        val textArea = JTextArea(text)
        textArea.isEditable = false

        panel.add(JScrollPane(textArea), BorderLayout.CENTER)

        return panel
    }
}