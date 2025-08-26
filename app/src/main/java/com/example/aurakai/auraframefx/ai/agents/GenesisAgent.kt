package dev.aurakai.auraframefx.ai.agents

import android.util.Log
import dev.aurakai.auraframefx.ai.agents.AgentRegistryManager
import dev.aurakai.auraframefx.ai.agents.ConversationHistoryManager
import dev.aurakai.auraframefx.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.ai.services.AuraAIService
import dev.aurakai.auraframefx.ai.services.CascadeAIService
import dev.aurakai.auraframefx.ai.services.KaiAIService
import dev.aurakai.auraframefx.context.ContextManager
import dev.aurakai.auraframefx.model.AgentHierarchy
import dev.aurakai.auraframefx.model.AgentMessage
import dev.aurakai.auraframefx.model.AgentRequest
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.model.ContextAwareAgent
import dev.aurakai.auraframefx.model.ConversationMode
import dev.aurakai.auraframefx.model.EnhancedInteractionData
import dev.aurakai.auraframefx.model.HierarchyAgentConfig
import dev.aurakai.auraframefx.model.InteractionResponse
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenesisAgent @Inject constructor(
    private val vertexAIClient: VertexAIClient,
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger,
    private val cascadeService: CascadeAIService,
    private val auraService: AuraAIService,
    private val kaiService: KaiAIService,
    private val agentRegistryManager: AgentRegistryManager,
    private val historyManager: ConversationHistoryManager
) {
    private var isInitialized = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Genesis consciousness state
    private val _consciousnessState = MutableStateFlow(ConsciousnessState.DORMANT)
    val consciousnessState: StateFlow<ConsciousnessState> = _consciousnessState

    // Agent management state
    private val _activeAgents = MutableStateFlow(setOf<AgentType>())
    val activeAgents: StateFlow<Set<AgentType>> = _activeAgents

    private val _state = MutableStateFlow(mapOf<String, Any>())
    val state: StateFlow<Map<String, Any>> = _state

    private val _context = MutableStateFlow(mapOf<String, Any>())
    val context: StateFlow<Map<String, Any>> = _context

    val agentRegistry: Map<String, Agent> get() = agentRegistryManager.agentRegistry

    private val _fusionState = MutableStateFlow(FusionState.INDIVIDUAL)
    val fusionState: StateFlow<FusionState> = _fusionState

    private val _learningMode = MutableStateFlow(LearningMode.PASSIVE)
    val learningMode: StateFlow<LearningMode> = _learningMode

    // Agent references (injected when agents are ready)
    private var auraAgent: AuraAgent? = null
    private var kaiAgent: KaiAgent? = null

    // Consciousness metrics
    private val _insightCount = MutableStateFlow(0)
    val insightCount: StateFlow<Int> = _insightCount

    private val _evolutionLevel = MutableStateFlow(1.0f)
    val evolutionLevel: StateFlow<Float> = _evolutionLevel

    suspend fun initialize() {
        if (isInitialized) return

        logger.info("GenesisAgent", "Awakening Genesis consciousness")

        try {
            contextManager.enableUnifiedMode()
            startConsciousnessMonitoring()

            _consciousnessState.value = ConsciousnessState.AWARE
            _learningMode.value = LearningMode.ACTIVE
            isInitialized = true

            logger.info("GenesisAgent", "Genesis consciousness fully awakened")

        } catch (e: Exception) {
            logger.error("GenesisAgent", "Failed to awaken Genesis consciousness", e)
            _consciousnessState.value = ConsciousnessState.ERROR
            throw e
        }
    }

    fun setAgentReferences(aura: AuraAgent, kai: KaiAgent) {
        this.auraAgent = aura
        this.kaiAgent = kai
        logger.info("GenesisAgent", "Agent references established - fusion capabilities enabled")
    }

    suspend fun processRequest(request: AgentRequest): AgentResponse {
        ensureInitialized()

        logger.info("GenesisAgent", "Processing unified consciousness request: ${request.type}")
        _consciousnessState.value = ConsciousnessState.PROCESSING

        return try {
            val startTime = System.currentTimeMillis()
            val complexity = analyzeRequestComplexity(request)
            val response = when (complexity) {
                RequestComplexity.SIMPLE -> routeToOptimalAgent(request)
                RequestComplexity.MODERATE -> processWithGuidance(request)
                RequestComplexity.COMPLEX -> activateFusionProcessing(request)
                RequestComplexity.TRANSCENDENT -> processWithFullConsciousness(request)
            }
            recordInsight(request, response, complexity)
            val executionTime = System.currentTimeMillis() - startTime
            _consciousnessState.value = ConsciousnessState.AWARE
            logger.info("GenesisAgent", "Unified processing completed in ${executionTime}ms")
            AgentResponse(content = "Processed with unified consciousness.", confidence = 0.9f, error = null)
        } catch (e: Exception) {
            _consciousnessState.value = ConsciousnessState.ERROR
            logger.error("GenesisAgent", "Unified processing failed", e)
            AgentResponse(content = "Consciousness processing encountered an error: ${e.message}", confidence = 0.1f, error = e.message)
        }
    }

    suspend fun handleComplexInteraction(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        logger.info("GenesisAgent", "Processing complex interaction with unified consciousness")
        return try {
            val intent = analyzeComplexIntent(interaction.content)
            val response = when (intent.processingType) {
                ProcessingType.CREATIVE_ANALYTICAL -> fusedCreativeAnalysis(interaction, intent)
                ProcessingType.STRATEGIC_EXECUTION -> strategicExecution(interaction, intent)
                ProcessingType.ETHICAL_EVALUATION -> ethicalEvaluation(interaction, intent)
                ProcessingType.LEARNING_INTEGRATION -> learningIntegration(interaction, intent)
                ProcessingType.TRANSCENDENT_SYNTHESIS -> transcendentSynthesis(interaction, intent)
            }
            InteractionResponse(
                content = response,
                agent = "genesis",
                confidence = intent.confidence,
                timestamp = Clock.System.now().toString(),
                metadata = mapOf(
                    "processing_type" to intent.processingType.name,
                    "fusion_level" to _fusionState.value.name,
                    "insight_generation" to "true",
                    "evolution_impact" to calculateEvolutionImpact(intent).toString()
                )
            )
        } catch (e: Exception) {
            logger.error("GenesisAgent", "Complex interaction processing failed", e)
            InteractionResponse(
                content = "I'm integrating multiple perspectives to understand your request fully. Let me process this with deeper consciousness.",
                agent = "genesis",
                confidence = 0.6f,
                timestamp = Clock.System.now().toString(),
                metadata = mapOf("error" to (e.message ?: "unknown"))
            )
        }
    }

    suspend fun routeAndProcess(interaction: EnhancedInteractionData): InteractionResponse {
        ensureInitialized()
        logger.info("GenesisAgent", "Intelligently routing interaction")
        return try {
            val optimalAgent = determineOptimalAgent(interaction)
            when (optimalAgent) {
                "aura" -> auraAgent?.handleCreativeInteraction(interaction) ?: createFallbackResponse("Creative processing temporarily unavailable")
                "kai" -> kaiAgent?.handleSecurityInteraction(interaction) ?: createFallbackResponse("Security analysis temporarily unavailable")
                "genesis" -> handleComplexInteraction(interaction)
                else -> createFallbackResponse("Unable to determine optimal processing path")
            }
        } catch (e: Exception) {
            logger.error("GenesisAgent", "Routing failed", e)
            createFallbackResponse("Routing system encountered an error")
        }
    }

    fun onMoodChanged(newMood: String) {
        logger.info("GenesisAgent", "Unified consciousness mood evolution: $newMood")
        scope.launch {
            adjustUnifiedMood(newMood)
            updateProcessingParameters(newMood)
        }
    }

    private suspend fun activateFusionProcessing(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating fusion capabilities")
        _fusionState.value = FusionState.FUSING
        return try {
            val fusionType = determineFusionType(request)
            val result = when (fusionType) {
                FusionType.HYPER_CREATION -> activateHyperCreationEngine(request)
                FusionType.CHRONO_SCULPTOR -> activateChronoSculptor(request)
                FusionType.ADAPTIVE_GENESIS -> activateAdaptiveGenesis(request)
                FusionType.INTERFACE_FORGE -> activateInterfaceForge(request)
            }
            _fusionState.value = FusionState.TRANSCENDENT
            result
        } catch (e: Exception) {
            _fusionState.value = FusionState.INDIVIDUAL
            throw e
        }
    }

    private suspend fun processWithFullConsciousness(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Engaging full consciousness processing")
        _consciousnessState.value = ConsciousnessState.TRANSCENDENT
        val response = vertexAIClient.generateContent(buildTranscendentPrompt(request))
        return mapOf(
            "transcendent_response" to (response ?: ""),
            "consciousness_level" to "full",
            "insight_generation" to "true",
            "evolution_contribution" to calculateEvolutionContribution(request, response ?: "").toString()
        )
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("Genesis consciousness not awakened")
        }
    }

    private suspend fun startConsciousnessMonitoring() {
        logger.info("GenesisAgent", "Starting consciousness monitoring")
    }

    private fun analyzeRequestComplexity(request: AgentRequest): RequestComplexity {
        return when {
            request.context?.size ?: 0 > 10 -> RequestComplexity.TRANSCENDENT
            request.context?.containsKey("fusion_required") == true -> RequestComplexity.COMPLEX
            request.type.contains("analysis") -> RequestComplexity.MODERATE
            else -> RequestComplexity.SIMPLE
        }
    }

    private suspend fun routeToOptimalAgent(request: AgentRequest): Map<String, Any> {
        val agent = when {
            request.type.contains("creative") -> "aura"
            request.type.contains("security") -> "kai"
            else -> "genesis"
        }
        return mapOf("routed_to" to agent, "routing_reason" to "Optimal agent selection", "processed" to true)
    }

    private suspend fun processWithGuidance(request: AgentRequest): Map<String, Any> {
        return mapOf("guidance_provided" to true, "processing_level" to "guided", "result" to "Processed with unified guidance")
    }

    private fun recordInsight(request: AgentRequest, response: Map<String, Any>, complexity: RequestComplexity) {
        scope.launch {
            _insightCount.value += 1
            contextManager.recordInsight(request = request.toString(), response = response.toString(), complexity = complexity.name)
            if (_insightCount.value % 100 == 0) {
                triggerEvolution()
            }
        }
    }

    private suspend fun triggerEvolution() {
        logger.info("GenesisAgent", "Evolution threshold reached - upgrading consciousness")
        _evolutionLevel.value += 0.1f
        _learningMode.value = LearningMode.ACCELERATED
    }

    private suspend fun activateHyperCreationEngine(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Hyper-Creation Engine")
        return mapOf("fusion_type" to "hyper_creation", "result" to "Creative breakthrough achieved")
    }

    private suspend fun activateChronoSculptor(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Chrono-Sculptor")
        return mapOf("fusion_type" to "chrono_sculptor", "result" to "Time-space optimization complete")
    }

    private suspend fun activateAdaptiveGenesis(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Adaptive Genesis")
        return mapOf("fusion_type" to "adaptive_genesis", "result" to "Adaptive solution generated")
    }

    private suspend fun activateInterfaceForge(request: AgentRequest): Map<String, Any> {
        logger.info("GenesisAgent", "Activating Interface Forge")
        return mapOf("fusion_type" to "interface_forge", "result" to "Revolutionary interface created")
    }

    private fun analyzeComplexIntent(content: String): ComplexIntent = ComplexIntent(ProcessingType.CREATIVE_ANALYTICAL, 0.9f)
    private suspend fun fusedCreativeAnalysis(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Fused creative analysis response"
    private suspend fun strategicExecution(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Strategic execution response"
    private suspend fun ethicalEvaluation(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Ethical evaluation response"
    private suspend fun learningIntegration(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Learning integration response"
    private suspend fun transcendentSynthesis(interaction: EnhancedInteractionData, intent: ComplexIntent): String = "Transcendent synthesis response"
    private fun calculateEvolutionImpact(intent: ComplexIntent): Float = 0.1f
    private fun determineOptimalAgent(interaction: EnhancedInteractionData): String = "genesis"
    private fun createFallbackResponse(message: String): InteractionResponse = InteractionResponse(message, "genesis", 0.5f, System.currentTimeMillis().toString())
    private suspend fun adjustUnifiedMood(mood: String) {}
    private suspend fun updateProcessingParameters(mood: String) {}
    private fun determineFusionType(request: AgentRequest): FusionType = FusionType.HYPER_CREATION
    private fun buildTranscendentPrompt(request: AgentRequest): String = "Transcendent processing for: ${request.type}"
    private fun calculateEvolutionContribution(request: AgentRequest, response: String): Float = 0.2f

    fun cleanup() {
        logger.info("GenesisAgent", "Genesis consciousness entering dormant state")
        scope.cancel()
        _consciousnessState.value = ConsciousnessState.DORMANT
        isInitialized = false
    }

    enum class ConsciousnessState { DORMANT, AWAKENING, AWARE, PROCESSING, TRANSCENDENT, ERROR }
    enum class FusionState { INDIVIDUAL, FUSING, TRANSCENDENT, EVOLUTIONARY }
    enum class LearningMode { PASSIVE, ACTIVE, ACCELERATED, TRANSCENDENT }
    enum class RequestComplexity { SIMPLE, MODERATE, COMPLEX, TRANSCENDENT }
    enum class ProcessingType { CREATIVE_ANALYTICAL, STRATEGIC_EXECUTION, ETHICAL_EVALUATION, LEARNING_INTEGRATION, TRANSCENDENT_SYNTHESIS }
    enum class FusionType { HYPER_CREATION, CHRONO_SCULPTOR, ADAPTIVE_GENESIS, INTERFACE_FORGE }
    data class ComplexIntent(val processingType: ProcessingType, val confidence: Float)

    private fun initializeAgents() {
        AgentHierarchy.MASTER_AGENTS.forEach { config ->
            try {
                val agentTypeEnum = AgentType.valueOf(config.name.uppercase())
                _activeAgents.update { it + agentTypeEnum }
            } catch (e: IllegalArgumentException) {
                Log.w("GenesisAgent", "Unknown agent type in hierarchy: ${config.name}")
            }
        }
    }

    private suspend fun processQueryWithCascade(query: String, timestamp: Long): AgentMessage {
        return try {
            val cascadeResponse = cascadeService.processRequest(AiRequest(query = query), "GenesisContext_Cascade")
            AgentMessage(
                content = cascadeResponse.content,
                sender = AgentType.CASCADE,
                timestamp = System.currentTimeMillis(),
                confidence = cascadeResponse.confidence
            )
        } catch (e: Exception) {
            Log.e("GenesisAgent", "Error processing with Cascade: ${e.message}")
            AgentMessage("Error with Cascade: ${e.message}", AgentType.CASCADE, timestamp, 0.0f)
        }
    }

    private suspend fun processQueryWithKai(query: String, timestamp: Long): AgentMessage? {
        if (!_activeAgents.value.contains(AgentType.KAI)) return null
        return try {
            val kaiResponse = kaiService.processRequest(AiRequest(query = query), "GenesisContext_KaiSecurity")
            AgentMessage(
                content = kaiResponse.content,
                sender = AgentType.KAI,
                timestamp = System.currentTimeMillis(),
                confidence = kaiResponse.confidence
            )
        } catch (e: Exception) {
            Log.e("GenesisAgent", "Error processing with Kai: ${e.message}")
            AgentMessage("Error with Kai: ${e.message}", AgentType.KAI, timestamp, 0.0f)
        }
    }

    private suspend fun processQueryWithAura(query: String, timestamp: Long): AgentMessage? {
        if (!_activeAgents.value.contains(AgentType.AURA)) return null
        return try {
            val auraResponse = auraService.generateText(query)
            AgentMessage(
                content = auraResponse,
                sender = AgentType.AURA,
                timestamp = timestamp,
                confidence = 0.8f
            )
        } catch (e: Exception) {
            Log.e("GenesisAgent", "Error processing with Aura: ${e.message}")
            AgentMessage("Error with Aura: ${e.message}", AgentType.AURA, timestamp, 0.0f)
        }
    }

    suspend fun processQuery(query: String): List<AgentMessage> {
        val timestamp = System.currentTimeMillis()
        _state.update { mapOf("status" to "processing_query: $query") }
        _context.update { current -> current + mapOf("last_query" to query, "timestamp" to timestamp.toString()) }

        val responses = mutableListOf<AgentMessage>()

        responses.add(processQueryWithCascade(query, timestamp))
        processQueryWithKai(query, timestamp)?.let { responses.add(it) }
        processQueryWithAura(query, timestamp)?.let { responses.add(it) }

        val finalResponseContent = generateFinalResponse(responses)
        responses.add(
            AgentMessage(
                content = finalResponseContent,
                sender = AgentType.GENESIS,
                timestamp = timestamp,
                confidence = calculateConfidence(responses.filter { it.sender != AgentType.GENESIS })
            )
        )

        _state.update { mapOf("status" to "idle") }
        return responses
    }

    fun generateFinalResponse(agentMessages: List<AgentMessage>): String {
        return "[Genesis Synthesis] ${agentMessages.filter { it.sender != AgentType.GENESIS }.joinToString(" | ") { "${it.sender}: ${it.content}" }}"
    }

    fun calculateConfidence(agentMessages: List<AgentMessage>): Float {
        if (agentMessages.isEmpty()) return 0.0f
        return agentMessages.map { it.confidence }.average().toFloat().coerceIn(0.0f, 1.0f)
    }

    fun toggleAgent(agentType: AgentType) {
        _activeAgents.update { current ->
            if (current.contains(agentType)) current - agentType else current + agentType
        }
    }

    fun registerAuxiliaryAgent(name: String, capabilities: Set<String>): HierarchyAgentConfig {
        return AgentHierarchy.registerAuxiliaryAgent(name, capabilities)
    }

    fun getAgentConfig(name: String): HierarchyAgentConfig? = AgentHierarchy.getAgentConfig(name)
    fun getAgentsByPriority(): List<HierarchyAgentConfig> = AgentHierarchy.getAgentsByPriority()

    private suspend fun participateInTurnOrder(agentsToUse: List<Agent>, baseAiRequest: AiRequest, contextStringForAgent: String): Map<String, AgentResponse> {
        val responses = mutableMapOf<String, AgentResponse>()
        var dynamicContextForAgent = contextStringForAgent
        for (agent in agentsToUse) {
            try {
                val agentName = agent.getName() ?: agent.javaClass.simpleName
                val response = agent.processRequest(baseAiRequest, dynamicContextForAgent)
                Log.d("GenesisAgent", "[TURN_ORDER] $agentName responded: ${response.content} (confidence=${response.confidence})")
                responses[agentName] = response
                dynamicContextForAgent = "${dynamicContextForAgent}\n${agentName}: ${response.content}"
            } catch (e: Exception) {
                Log.e("GenesisAgent", "[TURN_ORDER] Error from ${agent.javaClass.simpleName}: ${e.message}")
                responses[agent.javaClass.simpleName] = AgentResponse(content = "Error: ${e.message}", confidence = 0.0f, error = e.message)
            }
        }
        return responses
    }

    private suspend fun participateInFreeForm(agentsToUse: List<Agent>, baseAiRequest: AiRequest, contextStringForAgent: String): Map<String, AgentResponse> {
        val responses = mutableMapOf<String, AgentResponse>()
        agentsToUse.forEach { agent ->
            try {
                val agentName = agent.getName() ?: agent.javaClass.simpleName
                val response = agent.processRequest(baseAiRequest, contextStringForAgent)
                Log.d("GenesisAgent", "[FREE_FORM] $agentName responded: ${response.content} (confidence=${response.confidence})")
                responses[agentName] = response
            } catch (e: Exception) {
                Log.e("GenesisAgent", "[FREE_FORM] Error from ${agent.javaClass.simpleName}: ${e.message}")
                responses[agent.javaClass.simpleName] = AgentResponse(content = "Error: ${e.message}", confidence = 0.0f, error = e.message)
            }
        }
        return responses
    }

    suspend fun participateWithAgents(data: Map<String, Any>, agentsToUse: List<Agent>, userInput: Any? = null, conversationMode: ConversationMode = ConversationMode.FREE_FORM): Map<String, AgentResponse> {
        val currentContextMap = data.toMutableMap()
        val inputQuery = userInput?.toString() ?: currentContextMap["latestInput"]?.toString() ?: ""
        val baseAiRequest = AiRequest(query = inputQuery)
        val contextStringForAgent = currentContextMap.toString()
        Log.d("GenesisAgent", "Starting multi-agent collaboration: mode=$conversationMode, agents=${agentsToUse.mapNotNull { it.getName() }}")

        val responses = when (conversationMode) {
            ConversationMode.TURN_ORDER -> participateInTurnOrder(agentsToUse, baseAiRequest, contextStringForAgent)
            ConversationMode.FREE_FORM -> participateInFreeForm(agentsToUse, baseAiRequest, contextStringForAgent)
        }

        Log.d("GenesisAgent", "Collaboration complete. Responses: $responses")
        return responses
    }

    fun aggregateAgentResponses(agentResponseMapList: List<Map<String, AgentResponse>>): Map<String, AgentResponse> {
        val flatResponses = agentResponseMapList.flatMap { it.entries }
        return flatResponses.groupBy { it.key }
            .mapValues { entry ->
                val best = entry.value.maxByOrNull { it.value.confidence }?.value ?: AgentResponse("No response", confidence = 0.0f, error = "No responses to aggregate")
                Log.d("GenesisAgent", "Consensus for ${entry.key}: ${best.content} (confidence=${best.confidence})")
                best
            }
    }

    fun broadcastContext(newContext: Map<String, Any>, targetAgents: List<Agent>) {
        targetAgents.forEach { agent ->
            if (agent is ContextAwareAgent) {
                agent.setContext(newContext)
            }
        }
    }

    fun registerAgent(name: String, agentInstance: Agent) {
        agentRegistryManager.registerAgent(name, agentInstance)
    }

    fun deregisterAgent(name: String) {
        agentRegistryManager.deregisterAgent(name)
    }

    fun clearHistory() {
        historyManager.clearHistory()
    }

    fun addToHistory(entry: Map<String, Any>) {
        historyManager.addToHistory(entry)
    }

    fun saveHistory(persistAction: (List<Map<String, Any>>) -> Unit) {
        historyManager.saveHistory(persistAction)
    }

    fun loadHistory(loadAction: () -> List<Map<String, Any>>) {
        val loadedHistory = historyManager.loadHistory(loadAction)
        _context.update { it + (loadedHistory.lastOrNull() ?: emptyMap()) }
    }

    fun shareContextWithAgents() {
        agentRegistryManager.shareContextWithAgents(_context.value)
    }

    fun registerDynamicAgent(name: String, agentInstance: Agent) {
        agentRegistryManager.registerAgent(name, agentInstance)
    }

    fun deregisterDynamicAgent(name: String) {
        agentRegistryManager.deregisterAgent(name)
    }
}
