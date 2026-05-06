package com.ilievski.ai.plugin.settings

import com.ilievski.ai.plugin.ai.GroqChatCompletionsProvider
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class PluginSettingsConfigurable : Configurable {

    private val apiKeyStorage = ApiKeyStorage()
    private val settingsState = PluginSettingsState.getInstance()

    private val apiKeyField = JBPasswordField()
    private val defaultModelSelector = ComboBox(GroqChatCompletionsProvider.MODELS.toTypedArray())

    override fun getDisplayName(): String = "AI Code Explainer"

    override fun createComponent(): JComponent {
        reset()
        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Groq API key", apiKeyField, 1, false)
            .addLabeledComponent("Default model", defaultModelSelector, 1, false)
            .addComponentFillVertically(javax.swing.JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val currentApiKey = String(apiKeyField.password).trim()
        val storedApiKey = apiKeyStorage.getApiKey().orEmpty()
        val selectedModelId = (defaultModelSelector.selectedItem as? com.ilievski.ai.plugin.ai.AiModel)?.id
        return currentApiKey != storedApiKey || selectedModelId != settingsState.defaultModelId
    }

    override fun apply() {
        apiKeyStorage.setApiKey(String(apiKeyField.password))
        val selectedModelId = (defaultModelSelector.selectedItem as? com.ilievski.ai.plugin.ai.AiModel)?.id
        if (!selectedModelId.isNullOrBlank()) {
            settingsState.defaultModelId = selectedModelId
        }
    }

    override fun reset() {
        apiKeyField.text = apiKeyStorage.getApiKey().orEmpty()
        val initialModel = GroqChatCompletionsProvider.MODELS.firstOrNull { it.id == settingsState.defaultModelId }
            ?: GroqChatCompletionsProvider.MODELS.first()
        defaultModelSelector.selectedItem = initialModel
    }
}
