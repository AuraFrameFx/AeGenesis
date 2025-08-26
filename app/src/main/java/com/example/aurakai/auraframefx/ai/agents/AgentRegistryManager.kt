package dev.aurakai.auraframefx.ai.agents

import android.util.Log
import dev.aurakai.auraframefx.model.ContextAwareAgent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRegistryManager @Inject constructor() {
    private val _agentRegistry = mutableMapOf<String, Agent>()
    val agentRegistry: Map<String, Agent> get() = _agentRegistry

    fun registerAgent(name: String, agentInstance: Agent) {
        _agentRegistry[name] = agentInstance
        Log.d("GenesisAgent", "Registered agent: $name")
    }

    fun deregisterAgent(name: String) {
        _agentRegistry.remove(name)
        Log.d("GenesisAgent", "Deregistered agent: $name")
    }

    fun shareContextWithAgents(context: Map<String, Any>) {
        agentRegistry.values.forEach { agent ->
            if (agent is ContextAwareAgent) {
                agent.setContext(context)
            }
        }
    }
}
