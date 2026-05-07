package com.ilievski.ai.plugin.settings

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe

class ApiKeyStorage {

    companion object {
        private const val SERVICE_NAME_PREFIX = "AI Code Explainer API Key"
        private const val CREDENTIAL_USER = "api-key"
    }

    private fun attributesFor(id: String): CredentialAttributes = CredentialAttributes("$SERVICE_NAME_PREFIX:$id")

    fun getApiKey(id: String): String? {
        return PasswordSafe.instance.get(attributesFor(id))?.getPasswordAsString()?.trim().takeUnless { it.isNullOrBlank() }
    }

    fun setApiKey(id: String, apiKey: String?) {
        val normalized = apiKey?.trim().orEmpty()
        val credentials = if (normalized.isBlank()) null else Credentials(CREDENTIAL_USER, normalized)
        PasswordSafe.instance.set(attributesFor(id), credentials)
    }
}
