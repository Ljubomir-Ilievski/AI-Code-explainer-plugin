package com.ilievski.ai.plugin.service

import com.ilievski.ai.plugin.ai.AiClient

class ExplainService {

    private val client = AiClient()

    fun explain(code: String): String {
        return client.explain(code)
    }
}