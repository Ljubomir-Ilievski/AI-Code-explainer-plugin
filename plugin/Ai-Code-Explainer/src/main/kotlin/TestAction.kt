package com.ilievski.ai.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.pom.Navigatable
import org.jetbrains.annotations.NotNull


class TestAction : AnAction() {
    override fun actionPerformed(@NotNull event: AnActionEvent) {
        // Using the event, create and show a dialog
        val currentProject = event.project
        val message =
            StringBuilder(event.presentation.text + " Selected!")
        // If an element is selected in the editor, add info about it.
        val selectedElement = event.getData<Navigatable?>(CommonDataKeys.NAVIGATABLE)
        if (selectedElement != null) {
            message.append("\nSelected Element: ").append(selectedElement)
        }
        val title = event.getPresentation().getDescription()
        Messages.showMessageDialog(
            currentProject,
            message.toString(),
            title,
            Messages.getInformationIcon()
        )
    }
}