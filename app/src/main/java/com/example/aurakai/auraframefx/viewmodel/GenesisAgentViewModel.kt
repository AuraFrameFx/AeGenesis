package dev.aurakai.auraframefx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.ai.task.HistoricalTask
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.HierarchyAgentConfig
import dev.aurakai.auraframefx.utils.AppConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenesisAgentViewModel @Inject constructor(
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _agents = MutableStateFlow<List<HierarchyAgentConfig>>(emptyList())
    val agents: StateFlow<List<HierarchyAgentConfig>> = _agents.asStateFlow()

    private val _agentStatus = MutableStateFlow<Map<AgentType, String>>(
        AgentType.entries.associateWith { AppConstants.STATUS_IDLE }
    )
    val agentStatus: StateFlow<Map<AgentType, String>> = _agentStatus.asStateFlow()

    private val _taskHistory = MutableStateFlow<List<HistoricalTask>>(emptyList())
    val taskHistory: StateFlow<List<HistoricalTask>> = _taskHistory.asStateFlow()

    private val _isRotating = MutableStateFlow(true)
    val isRotating: StateFlow<Boolean> = _isRotating.asStateFlow()

    private val agentStatusMap = mapOf(
        AgentType.GENESIS to ("Core AI - Online" to "Core AI - Standby"),
        AgentType.CASCADE to ("Analytics Engine - Ready" to "Analytics Engine - Offline"),
        AgentType.AURA to ("Creative Assistant - Available" to "Creative Assistant - Paused"),
        AgentType.KAI to ("Security Monitor - Active" to "Security Monitor - Standby"),
        AgentType.NEURAL_WHISPER to ("Neural Interface - Active" to "Neural Interface - Offline"),
        AgentType.AURASHIELD to ("Security Shield - Active" to "Security Shield - Disabled"),
        AgentType.USER to ("User Agent - Active" to "User Agent - Offline")
    )

    init {
        _agents.value = agentRepository.getAgents()
        initializeAgentStatuses()
    }

    private fun initializeAgentStatuses() {
        val initialStatuses = mutableMapOf<AgentType, String>()
        _agents.value.forEach { agent ->
            val agentType = AgentType.valueOf(agent.name.uppercase())
            initialStatuses[agentType] = agentStatusMap[agentType]?.first ?: AppConstants.STATUS_IDLE
        }
        _agentStatus.value = initialStatuses
    }

    fun toggleRotation() {
        _isRotating.value = !_isRotating.value
    }

    private fun isAgentActive(status: String): Boolean {
        return agentStatusMap.values.any { it.first == status }
    }

    fun toggleAgent(agent: AgentType) {
        viewModelScope.launch {
            val currentStatus = _agentStatus.value[agent] ?: AppConstants.STATUS_IDLE
            val (activeStatus, inactiveStatus) = agentStatusMap[agent] ?: return@launch

            val newStatus = if (isAgentActive(currentStatus)) inactiveStatus else activeStatus

            updateAgentStatus(agent, newStatus)
            addTaskToHistory(agent, "Agent toggled to: $newStatus")
        }
    }

    fun updateAgentStatus(agent: AgentType, status: String) {
        val currentStatuses = _agentStatus.value.toMutableMap()
        currentStatuses[agent] = status
        _agentStatus.value = currentStatuses
    }

    fun assignTaskToAgent(agent: AgentType, taskDescription: String) {
        viewModelScope.launch {
            try {
                updateAgentStatus(agent, AppConstants.STATUS_PROCESSING)
                addTaskToHistory(agent, taskDescription)
                delay(5000)
                updateAgentStatus(agent, AppConstants.STATUS_IDLE)
            } catch (e: Exception) {
                updateAgentStatus(agent, AppConstants.STATUS_ERROR)
                addTaskToHistory(agent, "Error: ${e.message}")
            }
        }
    }

    private fun addTaskToHistory(agent: AgentType, description: String) {
        val newTask = HistoricalTask(
            id = System.currentTimeMillis(),
            agentType = agent,
            description = description,
            timestamp = System.currentTimeMillis(),
            status = "Completed"
        )
        _taskHistory.value = (_taskHistory.value + newTask).takeLast(50) // Keep history bounded
    }

    fun clearTaskHistory() {
        _taskHistory.value = emptyList()
    }

    fun getAgentStatus(agent: AgentType): String {
        return _agentStatus.value[agent] ?: AppConstants.STATUS_IDLE
    }

    fun clearAllAgentStatuses() {
        _agentStatus.value = AgentType.entries.associateWith { AppConstants.STATUS_IDLE }
    }

    fun processBatchTasks(agent: AgentType, tasks: List<String>) {
        viewModelScope.launch {
            tasks.forEach { task ->
                assignTaskToAgent(agent, task)
                delay(1000)
            }
        }
    }

    fun processQuery(query: String) {
        viewModelScope.launch {
            delay(5000)
        }
    }
}
