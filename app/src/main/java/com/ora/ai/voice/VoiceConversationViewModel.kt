package com.ora.ai.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceConversationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val voiceManager =
        VoiceConversationManager(application.applicationContext)

    private val _state =
        MutableStateFlow(VoiceConversationState())

    val state: StateFlow<VoiceConversationState> =
        _state.asStateFlow()

    fun startListening(
        onTextResult: (String) -> Unit
    ) {

        _state.value = _state.value.copy(
            voiceState = VoiceState.LISTENING,
            isMicrophoneEnabled = true,
            errorMessage = ""
        )

        voiceManager.startListening(

            onTextResult = { text ->

                _state.value = _state.value.copy(
                    voiceState = VoiceState.PROCESSING,
                    recognizedText = text,
                    isMicrophoneEnabled = false,
                    errorMessage = ""
                )

                viewModelScope.launch {
                    onTextResult(text)
                }
            },

            onError = { error ->

                _state.value = _state.value.copy(
                    voiceState = VoiceState.ERROR,
                    isMicrophoneEnabled = false,
                    errorMessage = error
                )
            }
        )
    }

    fun speak(text: String) {

        if (text.isBlank()) {
            return
        }

        _state.value = _state.value.copy(
            voiceState = VoiceState.SPEAKING,
            isSpeaking = true,
            errorMessage = ""
        )

        voiceManager.speak(text)
    }

    fun stopSpeaking() {

        voiceManager.stopSpeaking()

        _state.value = _state.value.copy(
            voiceState = VoiceState.IDLE,
            isSpeaking = false
        )
    }

    fun stopListening() {

        voiceManager.stopListening()

        _state.value = _state.value.copy(
            voiceState = VoiceState.IDLE,
            isMicrophoneEnabled = false
        )
    }

    fun clearError() {

        _state.value = _state.value.copy(
            voiceState = VoiceState.IDLE,
            errorMessage = ""
        )
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
