package com.ilievski.ai.plugin.settings

import com.ilievski.ai.plugin.ai.AiProviderRegistry
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class PluginSettingsConfigurable : Configurable {

    private val apiKeyStorage = ApiKeyStorage()
    private val settingsState = PluginSettingsState.getInstance()

    private val providerApiKeyIds = AiProviderRegistry.models
        .map { it.apiKeyId }
        .distinct()
    private val apiKeyFields = providerApiKeyIds.associateWith { JBPasswordField() }
    private val defaultModelSelector = ComboBox(AiProviderRegistry.models.toTypedArray())

    override fun getDisplayName(): String = "AI Code Explainer"

    override fun createComponent(): JComponent {
        reset()
        val builder = FormBuilder.createFormBuilder()
        providerApiKeyIds.forEach { keyId ->
            val label = "${keyId.replaceFirstChar { it.uppercase() }} API key"
            builder.addLabeledComponent(label, apiKeyFields.getValue(keyId), 1, false)
        }

        return builder
            .addLabeledComponent("Default model", defaultModelSelector, 1, false)
            .addComponentFillVertically(javax.swing.JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val keysModified = providerApiKeyIds.any { keyId ->
            val currentApiKey = String(apiKeyFields.getValue(keyId).password).trim()
            val storedApiKey = apiKeyStorage.getApiKey(keyId).orEmpty()
            currentApiKey != storedApiKey
        }
        val selectedModelId = (defaultModelSelector.selectedItem as? com.ilievski.ai.plugin.ai.AiModel)?.id
        return keysModified || selectedModelId != settingsState.defaultModelId
    }

    override fun apply() {
        providerApiKeyIds.forEach { keyId ->
            apiKeyStorage.setApiKey(keyId, String(apiKeyFields.getValue(keyId).password))
        }
        val selectedModelId = (defaultModelSelector.selectedItem as? com.ilievski.ai.plugin.ai.AiModel)?.id
        if (!selectedModelId.isNullOrBlank()) {
            settingsState.defaultModelId = selectedModelId
        }
    }

    override fun reset() {
        providerApiKeyIds.forEach { keyId ->
            apiKeyFields.getValue(keyId).text = apiKeyStorage.getApiKey(keyId).orEmpty()
        }
        val initialModel = AiProviderRegistry.models.firstOrNull { it.id == settingsState.defaultModelId }
            ?: AiProviderRegistry.models.first()
        defaultModelSelector.selectedItem = initialModel
    }
}
