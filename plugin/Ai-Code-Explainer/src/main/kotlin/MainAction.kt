package com.ilievski.ai.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import org.jetbrains.annotations.NotNull
import com.ilievski.ai.plugin.ai.AiModel
import com.ilievski.ai.plugin.ai.OpenAIResponsesProvider
import com.ilievski.ai.plugin.service.ExplainService
import com.ilievski.ai.plugin.ui.ExplainDialog
import javax.swing.SwingUtilities


class MainAction : AnAction() {
    private val explainService = ExplainService()
    private val defaultModel = OpenAIResponsesProvider.MODELS.first()

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)

        if (editor == null) {
            Messages.showErrorDialog("No editor found", "Error")
            return
        }

        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText

        if (selectedText.isNullOrBlank()) {
            Messages.showInfoMessage("No text selected", "Info")
            return
        }

        lateinit var dialog: ExplainDialog
        dialog = ExplainDialog(
            models = OpenAIResponsesProvider.MODELS,
            initialModel = defaultModel
        ) { selectedModel ->
            requestExplanation(selectedText, dialog, selectedModel)
        }
        dialog.isVisible = true
        requestExplanation(selectedText, dialog, defaultModel)
    }

    private fun requestExplanation(selectedText: String, dialog: ExplainDialog, model: AiModel) {
        dialog.showLoadingState()
        ApplicationManager.getApplication().executeOnPooledThread {
            val explanation = explainService.explain(selectedText, model)
            SwingUtilities.invokeLater {
                if (dialog.isDisplayable) {
                    dialog.showExplanation(explanation)
                }
            }
        }
    }
    override fun update(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor?.selectionModel?.hasSelection() == true

        event.presentation.isEnabledAndVisible = hasSelection
    }
}