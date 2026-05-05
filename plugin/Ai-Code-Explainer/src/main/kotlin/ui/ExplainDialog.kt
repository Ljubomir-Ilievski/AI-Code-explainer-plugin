package com.ilievski.ai.plugin.ui

import java.awt.BorderLayout
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.UIManager

class ExplainDialog : JDialog() {

    private val containerPanel = JPanel(BorderLayout())
    private val loadingLabel = JLabel("Generating explanation...", UIManager.getIcon("OptionPane.informationIcon"), JLabel.CENTER)
    private val responseTextArea = JTextArea()

    init {
        title = "AI Code Explanation"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(700, 450)
        setLocationRelativeTo(null)

        responseTextArea.isEditable = false

        contentPane.layout = BorderLayout()
        contentPane.add(containerPanel, BorderLayout.CENTER)

        showLoading()
    }

    private fun showLoading() {
        containerPanel.removeAll()
        containerPanel.add(loadingLabel, BorderLayout.CENTER)
        containerPanel.revalidate()
        containerPanel.repaint()
    }

    fun showExplanation(text: String) {
        responseTextArea.text = text
        responseTextArea.caretPosition = 0

        containerPanel.removeAll()
        containerPanel.add(JScrollPane(responseTextArea), BorderLayout.CENTER)
        containerPanel.revalidate()
        containerPanel.repaint()
    }

    override fun setVisible(visible: Boolean) {
        if (visible && owner == null) {
            val activeWindow = JFrame.getFrames().firstOrNull { it.isActive }
            if (activeWindow != null) {
                setLocationRelativeTo(activeWindow)
            }
        }
        super.setVisible(visible)
    }
}