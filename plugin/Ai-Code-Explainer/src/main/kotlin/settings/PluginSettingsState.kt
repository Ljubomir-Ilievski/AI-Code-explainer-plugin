package com.ilievski.ai.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.ilievski.ai.plugin.ai.AiProviderRegistry

@Service(Service.Level.APP)
@State(name = "AiCodeExplainerSettings", storages = [Storage("aiCodeExplainer.xml")])
class PluginSettingsState : PersistentStateComponent<PluginSettingsState.State> {

    data class State(
        var defaultModelId: String = AiProviderRegistry.models.first().id
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var defaultModelId: String
        get() = state.defaultModelId
        set(value) {
            state.defaultModelId = value
        }

    companion object {
        fun getInstance(): PluginSettingsState {
            return ApplicationManager.getApplication().getService(PluginSettingsState::class.java)
        }
    }
}
