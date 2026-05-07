package com.ilievski.ai.plugin.service

import com.ilievski.ai.plugin.ai.AiClient
import com.ilievski.ai.plugin.ai.AiModel
import com.ilievski.ai.plugin.ai.AiProviderRegistry

class ExplainService {
    fun explain(code: String, aiModel: AiModel, apiKey: String): String {
        val provider = AiProviderRegistry.getProvider(aiModel.providerId)
            ?: return "Unknown AI provider: ${aiModel.providerId}"

        val client = AiClient(provider)
        return client.explain(code, aiModel, apiKey)
    }
}