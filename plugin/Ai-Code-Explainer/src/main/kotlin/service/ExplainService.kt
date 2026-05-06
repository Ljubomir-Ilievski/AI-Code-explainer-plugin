package com.ilievski.ai.plugin.service

import com.ilievski.ai.plugin.ai.AiClient
import com.ilievski.ai.plugin.ai.AiModel
import com.ilievski.ai.plugin.ai.GroqChatCompletionsProvider

class ExplainService {

    private val client = AiClient(GroqChatCompletionsProvider())

    fun explain(code: String, aiModel: AiModel, apiKey: String): String {
        return client.explain(code, aiModel, apiKey)
    }
}