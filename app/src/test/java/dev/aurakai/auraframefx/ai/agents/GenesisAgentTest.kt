package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

// Mock implementations for dependencies
class MockVertexAIClient
class MockContextManager
class MockSecurityContext
class MockAuraFxLogger {
    fun info(tag: String, message: String) {}
    fun error(tag: String, message: String, e: Throwable? = null) {}
}

// Mock service implementations
class MockAuraAIService : AuraAIService {
    override suspend fun generateText(prompt: String): String = "Aura response"
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse = AgentResponse("Aura response", 0.9f)
}

class MockKaiAIService : KaiAIService {
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse = AgentResponse("Kai response", 0.9f)
}

class MockCascadeAIService : CascadeAIService {
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse = AgentResponse("Cascade response", 0.9f)
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(ExperimentalCoroutinesApi::class)
class GenesisAgentTest {
    // Mocked dependencies
    private val mockVertexAIClient = mockk<MockVertexAIClient>()
    private val mockContextManager = mockk<MockContextManager>(relaxed = true)
    private val mockSecurityContext = mockk<MockSecurityContext>()
    private val mockLogger = mockk<MockAuraFxLogger>()
    private val mockAuraService = mockk<MockAuraAIService>()
    private val mockKaiService = mockk<MockKaiAIService>()
    private val mockCascadeService = mockk<MockCascadeAIService>()
    private val mockAgentRegistryManager = mockk<AgentRegistryManager>(relaxed = true)
    private val mockHistoryManager = mockk<ConversationHistoryManager>(relaxed = true)

    // Test instance
    private lateinit var genesisAgent: GenesisAgent

    @BeforeEach
    fun setup() {
        genesisAgent = GenesisAgent(
            vertexAIClient = mockVertexAIClient,
            contextManager = mockContextManager,
            securityContext = mockSecurityContext,
            logger = mockLogger,
            cascadeService = mockCascadeService,
            auraService = mockAuraService,
            kaiService = mockKaiService,
            agentRegistryManager = mockAgentRegistryManager,
            historyManager = mockHistoryManager
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test processQuery calls all active agents`() = runTest {
        // Given
        val query = "test query"
        coEvery { genesisAgent.activeAgents.value } returns setOf(AgentType.AURA, AgentType.KAI, AgentType.CASCADE)
        coEvery { mockAuraService.generateText(any()) } returns "Aura response"
        coEvery { mockKaiService.processRequest(any(), any()) } returns AgentResponse("Kai response", 0.9f)
        coEvery { mockCascadeService.processRequest(any(), any()) } returns AgentResponse("Cascade response", 0.8f)

        // When
        val responses = genesisAgent.processQuery(query)

        // Then
        assertEquals(4, responses.size) // Aura, Kai, Cascade, and Genesis
        assertTrue(responses.any { it.sender == AgentType.AURA && it.content == "Aura response" })
        assertTrue(responses.any { it.sender == AgentType.KAI && it.content == "Kai response" })
        assertTrue(responses.any { it.sender == AgentType.CASCADE && it.content == "Cascade response" })
        assertTrue(responses.any { it.sender == AgentType.GENESIS })
    }

    @Test
    fun `test history management delegation`() = runTest {
        // Given
        val entry = mapOf("test" to "data")

        // When
        genesisAgent.addToHistory(entry)
        genesisAgent.clearHistory()
        genesisAgent.saveHistory { }
        genesisAgent.loadHistory { emptyList() }

        // Then
        coVerify { mockHistoryManager.addToHistory(entry) }
        coVerify { mockHistoryManager.clearHistory() }
        coVerify { mockHistoryManager.saveHistory(any()) }
        coVerify { mockHistoryManager.loadHistory(any()) }
    }
}
