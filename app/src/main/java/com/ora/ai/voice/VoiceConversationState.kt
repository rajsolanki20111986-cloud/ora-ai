package com.ora.ai.voice

data class VoiceConversationState(
    val voiceState: VoiceState = VoiceState.IDLE,
    val recognizedText: String = "",
    val errorMessage: String = "",
    val isMicrophoneEnabled: Boolean = false,
    val isSpeaking: Boolean = false
)
