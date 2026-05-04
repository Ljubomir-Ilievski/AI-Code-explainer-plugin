package com.ilievski.ai.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import org.jetbrains.annotations.NotNull
import com.ilievski.ai.plugin.service.ExplainService
import com.ilievski.ai.plugin.ui.ExplainDialog


class MainAction : AnAction() {
    private val explainService = ExplainService()

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
        val explanation = explainService.explain(selectedText)
        ExplainDialog(explanation).show()
    }
    override fun update(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor?.selectionModel?.hasSelection() == true

        event.presentation.isEnabledAndVisible = hasSelection
    }
}