package com.ora.ai.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ora.ai.data.GeminiApiService
import com.ora.ai.model.ChatMessage
import com.ora.ai.voice.TextToSpeechManager
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val geminiApiService = GeminiApiService()

    private val textToSpeechManager =
        TextToSpeechManager(application.applicationContext)

    private var nextMessageId = 1L

    val messages = mutableStateListOf<ChatMessage>()

    var messageText by mutableStateOf("")
        private set

    var isSending by mutableStateOf(false)
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

        if (isSending) {
            return
        }

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
                    isUser = false,
                    isError = true
                )
            )

            return
        }

        isSending = true

        val loadingMessageId = nextMessageId++

        messages.add(
            ChatMessage(
                id = loadingMessageId,
                text = "Thinking...",
                isUser = false,
                isLoading = true
            )
        )

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

                    textToSpeechManager.speak(response)
                }
                .onFailure { error ->

                    messages.add(
                        ChatMessage(
                            id = nextMessageId++,
                            text = buildErrorMessage(error),
                            isUser = false,
                            isError = true
                        )
                    )
                }

            isSending = false
        }
    }

    private fun buildErrorMessage(
        error: Throwable
    ): String {

        val message = error.message?.trim()

        return if (message.isNullOrBlank()) {
            "Sorry, ORA को response प्राप्त करने में समस्या हुई। कृपया फिर से कोशिश करें।"
        } else {
            "Sorry, ORA को response प्राप्त करने में समस्या हुई।\n\n$message"
        }
    }

    override fun onCleared() {
        textToSpeechManager.shutdown()
        super.onCleared()
    }
}
