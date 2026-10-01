package com.ora.ai.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
