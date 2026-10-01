package com.ora.ai.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ora.ai.model.ChatMessage

class ChatViewModel : ViewModel() {

    private var nextMessageId = 1L

    val messages = mutableStateListOf<ChatMessage>()

    var messageText by mutableStateOf("")
        private set

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
    }
}
