package dev.aurakai.auraframefx.core.dream

data class DreamCycle(
    val id: Long,
    val primaryType: DreamType,
    val secondaryType: DreamType,
    val intensity: Float,
    val coherence: Float,
    val timestamp: Long
)

data class Dream(
    val cycle: DreamCycle,
    val content: MutableList<String>,
    val insights: MutableList<String>,
    val connections: MutableList<Connection>
)

data class Connection(
    val source: String,
    val target: String,
    val strength: Float
)

data class DreamInsight(
    val content: String,
    val importance: Float,
    val timestamp: Long
)

data class EvolutionProjection(
    val name: String,
    val ability: String
)
