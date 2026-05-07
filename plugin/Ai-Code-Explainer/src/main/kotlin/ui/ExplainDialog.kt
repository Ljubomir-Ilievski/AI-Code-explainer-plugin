package com.ilievski.ai.plugin.ui

import com.ilievski.ai.plugin.ai.AiModel
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JList
import javax.swing.UIManager
import javax.swing.DefaultListCellRenderer
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

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
    private val modelPanel = JPanel()
    private val defaultModelLabel = JLabel("Default model")
    private val loadingLabel = JLabel("Generating explanation...", UIManager.getIcon("OptionPane.informationIcon"), JLabel.CENTER)
    private val responsePane = JEditorPane()
    private val modelSelector = JComboBox(models.toTypedArray())
    private val askAgainButton = JButton("Ask again")
    private val markdownParser = Parser.builder().build()
    private val htmlRenderer = HtmlRenderer.builder().build()

    init {
        title = "AI Code Explanation"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        setSize(700, 450)
        setLocationRelativeTo(null)

        responsePane.contentType = "text/html"
        responsePane.isEditable = false

        modelSelector.selectedItem = initialModel
        modelSelector.renderer = ModelCellRenderer()

        modelPanel.layout = BoxLayout(modelPanel, BoxLayout.X_AXIS)
        modelPanel.add(defaultModelLabel)
        modelPanel.add(modelSelector)

        topPanel.add(modelPanel)
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
        responsePane.text = renderMarkdownAsHtml(text)
        responsePane.caretPosition = 0

        containerPanel.removeAll()
        containerPanel.add(JScrollPane(responsePane), BorderLayout.CENTER)
        containerPanel.revalidate()
        containerPanel.repaint()
    }

    private fun renderMarkdownAsHtml(markdown: String): String {
        val document = markdownParser.parse(markdown)
        val body = htmlRenderer.render(document)

        return """
            <html>
              <body style="font-family: sans-serif; font-size: 12px;">
                $body
              </body>
            </html>
        """.trimIndent()
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
