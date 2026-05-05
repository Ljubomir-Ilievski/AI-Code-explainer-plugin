package com.ilievski.ai.plugin.ui

import com.ilievski.ai.plugin.ai.AiModel
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JList
import javax.swing.UIManager
import javax.swing.DefaultListCellRenderer

class ExplainDialog(
    models: List<AiModel>,
    initialModel: AiModel,
    private val onAskAgain: (AiModel) -> Unit
) : JDialog() {

    private class ModelCellRenderer : DefaultListCellRenderer() {
        override fun getListCellRendererComponent(
            list: JList<*>?,
            value: Any?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ) = super.getListCellRendererComponent(list, (value as? AiModel)?.id ?: "", index, isSelected, cellHasFocus)
    }

    private val containerPanel = JPanel(BorderLayout())
    private val topPanel = JPanel()
    private val loadingLabel = JLabel("Generating explanation...", UIManager.getIcon("OptionPane.informationIcon"), JLabel.CENTER)
    private val responseTextArea = JTextArea()
    private val modelSelector = JComboBox(models.toTypedArray())
    private val askAgainButton = JButton("Ask again")

    init {
        title = "AI Code Explanation"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(700, 450)
        setLocationRelativeTo(null)

        responseTextArea.isEditable = false

        modelSelector.selectedItem = initialModel
        modelSelector.renderer = ModelCellRenderer()

        topPanel.add(modelSelector)
        topPanel.add(askAgainButton)

        askAgainButton.addActionListener {
            val selectedModel = modelSelector.selectedItem as? AiModel ?: return@addActionListener
            onAskAgain(selectedModel)
        }

        contentPane.layout = BorderLayout()
        contentPane.add(topPanel, BorderLayout.NORTH)
        contentPane.add(containerPanel, BorderLayout.CENTER)

        showLoadingState()
    }

    fun showLoadingState() {
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