package com.ilievski.ai.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.ui.Messages
import org.jetbrains.annotations.NotNull
import com.ilievski.ai.plugin.ai.AiProviderRegistry
import com.ilievski.ai.plugin.ai.AiModel
import com.ilievski.ai.plugin.settings.ApiKeyStorage
import com.ilievski.ai.plugin.settings.PluginSettingsConfigurable
import com.ilievski.ai.plugin.settings.PluginSettingsState
import com.ilievski.ai.plugin.service.ExplainService
import com.ilievski.ai.plugin.ui.ExplainDialog
import javax.swing.SwingUtilities


class MainAction : AnAction() {
    private val explainService = ExplainService()
    private val apiKeyStorage = ApiKeyStorage()
    private val settingsState = PluginSettingsState.getInstance()
    private val availableModels = AiProviderRegistry.models

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

        val defaultModel = availableModels.firstOrNull { it.id == settingsState.defaultModelId }
            ?: availableModels.first()

        lateinit var dialog: ExplainDialog
        dialog = ExplainDialog(
            models = availableModels,
            initialModel = defaultModel
        ) { selectedModel ->
            requestExplanation(event.project, selectedText, dialog, selectedModel)
        }
        dialog.isVisible = true
        requestExplanation(event.project, selectedText, dialog, defaultModel)
    }

    private fun requestExplanation(project: com.intellij.openapi.project.Project?, selectedText: String, dialog: ExplainDialog, model: AiModel) {
        val apiKey = apiKeyStorage.getApiKey(model.apiKeyId)
        if (apiKey.isNullOrBlank()) {
            val result = Messages.showOkCancelDialog(
                dialog,
                "No API key found. Please add your ${model.apiKeyId} API key in plugin settings.",
                "AI Code Explainer",
                "Open Settings",
                "Cancel",
                Messages.getInformationIcon()
            )
            if (result == Messages.OK) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, PluginSettingsConfigurable::class.java)
            }
            return
        }

        settingsState.defaultModelId = model.id
        dialog.showLoadingState()
        ApplicationManager.getApplication().executeOnPooledThread {
            val explanation = explainService.explain(selectedText, model, apiKey)
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