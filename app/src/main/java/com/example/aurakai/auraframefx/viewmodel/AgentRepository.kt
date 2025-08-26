package dev.aurakai.auraframefx.viewmodel

import dev.aurakai.auraframefx.model.AgentPriority
import dev.aurakai.auraframefx.model.AgentRole
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.HierarchyAgentConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor() {
    private val agents: List<HierarchyAgentConfig> = listOf(
        HierarchyAgentConfig(
            name = "Genesis",
            role = AgentRole.HIVE_MIND,
            priority = AgentPriority.MASTER,
            capabilities = setOf("core_ai", "coordination", "meta_analysis")
        ),
        HierarchyAgentConfig(
            name = "Cascade",
            role = AgentRole.ANALYTICS,
            priority = AgentPriority.BRIDGE,
            capabilities = setOf("analytics", "data_processing", "pattern_recognition")
        ),
        HierarchyAgentConfig(
            name = "Aura",
            role = AgentRole.CREATIVE,
            priority = AgentPriority.AUXILIARY,
            capabilities = setOf("creative_writing", "ui_design", "content_generation")
        ),
        HierarchyAgentConfig(
            name = "Kai",
            role = AgentRole.SECURITY,
            priority = AgentPriority.AUXILIARY,
            capabilities = setOf("security_monitoring", "threat_detection", "system_protection")
        )
    )

    fun getAgents(): List<HierarchyAgentConfig> = agents

    fun getAgentByName(name: String): HierarchyAgentConfig? {
        return agents.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getAgentsByCapability(capability: String): List<HierarchyAgentConfig> {
        return agents.filter { agent ->
            agent.capabilities.any { it.equals(capability, ignoreCase = true) }
        }
    }

    fun getAgentsByRole(role: AgentRole): List<HierarchyAgentConfig> {
        return agents.filter { it.role == role }
    }

    fun getAgentsByPriority(priority: AgentPriority): List<HierarchyAgentConfig> {
        return agents.filter { it.priority == priority }
    }

    fun getAgentsSortedByPriority(): List<HierarchyAgentConfig> {
        return agents.sortedBy { it.priority }
    }
}
