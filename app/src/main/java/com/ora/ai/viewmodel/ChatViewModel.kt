package com.ora.ai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ora.ai.data.GeminiApiService
import com.ora.ai.model.ChatMessage
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val geminiApiService = GeminiApiService()

    private var nextMessageId = 1L

    val messages = mutableStateListOf<ChatMessage>()

    var messageText by mutableStateOf("")
        private set

    private var apiKey = ""

    init {
        messages.add(
            ChatMessage(
                id = nextMessageId++,
                text = "Hello! I'm ORA.\nHow can I help you?",
                isUser = false
            )
        )
    }

    fun updateMessageText(text: String) {
        messageText = text
    }

    fun setApiKey(key: String) {
        apiKey = key.trim()
    }

    fun sendMessage() {
        val text = messageText.trim()

        if (text.isEmpty()) {
            return
        }

        messages.add(
            ChatMessage(
                id = nextMessageId++,
                text = text,
                isUser = true
            )
        )

        messageText = ""

        if (apiKey.isBlank()) {
            messages.add(
                ChatMessage(
                    id = nextMessageId++,
                    text = "Gemini API key अभी सेट नहीं है। Settings में API key जोड़ें।",
                    isUser = false
                )
            )
            return
        }

        messages.add(
            ChatMessage(
                id = nextMessageId++,
                text = "Thinking...",
                isUser = false,
                isLoading = true
            )
        )

        val loadingMessageId = nextMessageId - 1

        viewModelScope.launch {

            val result = geminiApiService.generateText(
                apiKey = apiKey,
                userMessage = text
            )

            val loadingIndex = messages.indexOfFirst {
                it.id == loadingMessageId
            }

            if (loadingIndex >= 0) {
                messages.removeAt(loadingIndex)
            }

            result
                .onSuccess { response ->
                    messages.add(
                        ChatMessage(
                            id = nextMessageId++,
                            text = response,
                            isUser = false
                        )
                    )
                }
                .onFailure { error ->
                    messages.add(
                        ChatMessage(
                            id = nextMessageId++,
                            text = "Sorry, मुझे response प्राप्त करने में समस्या हुई।\n\n${error.message ?: "Unknown error"}",
                            isUser = false
                        )
                    )
                }
        }
    }
}
