package com.ilievski.ai.plugin.service

import com.ilievski.ai.plugin.ai.AiClient
import com.ilievski.ai.plugin.ai.AiModel
import com.ilievski.ai.plugin.ai.OpenAIResponsesProvider

class ExplainService {

    private val client = AiClient(OpenAIResponsesProvider())

    fun explain(code: String, aiModel: AiModel): String {
        return client.explain(code, aiModel)
    }
}