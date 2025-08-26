package dev.aurakai.auraframefx.ai.agents

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationHistoryManager @Inject constructor() {
    private val _history = mutableListOf<Map<String, Any>>()
    val history: List<Map<String, Any>> get() = _history

    fun clearHistory() {
        _history.clear()
        Log.d("GenesisAgent", "Cleared conversation history")
    }

    fun addToHistory(entry: Map<String, Any>) {
        _history.add(entry)
        Log.d("GenesisAgent", "Added to history: $entry")
    }

    fun saveHistory(persistAction: (List<Map<String, Any>>) -> Unit) {
        persistAction(_history)
    }

    fun loadHistory(loadAction: () -> List<Map<String, Any>>): List<Map<String, Any>> {
        val loadedHistory = loadAction()
        _history.clear()
        _history.addAll(loadedHistory)
        return loadedHistory
    }
}
