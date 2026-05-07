package com.ilievski.ai.plugin.ai

import com.ilievski.ai.plugin.ai.providers.AiProvider
import com.ilievski.ai.plugin.ai.providers.GeminiGenerateContentProvider
import com.ilievski.ai.plugin.ai.providers.GroqChatCompletionsProvider

object AiProviderRegistry {

    private val providers = mapOf(
        "groq" to GroqChatCompletionsProvider(),
        "gemini" to GeminiGenerateContentProvider(),
    )

    val models: List<AiModel> = listOf(
        GroqChatCompletionsProvider.MODELS,
        GeminiGenerateContentProvider.MODELS,
    ).flatten()

    fun getProvider(id: String): AiProvider? {
        return providers[id]
    }
}