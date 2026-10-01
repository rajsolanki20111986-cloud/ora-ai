package com.ora.ai.voice

import android.content.Context

class VoiceConversationManager(
    context: Context
) {

    private val speechToTextManager =
        SpeechToTextManager(context)

    private val textToSpeechManager =
        TextToSpeechManager(context)

    var voiceState: VoiceState =
        VoiceState.IDLE
        private set

    fun startListening(
        onTextResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        voiceState = VoiceState.LISTENING

        speechToTextManager.startListening(

            onResult = { text ->

                voiceState = VoiceState.PROCESSING

                onTextResult(text)
            },

            onError = { error ->

                voiceState = VoiceState.ERROR

                onError(error)

                voiceState = VoiceState.IDLE
            }
        )
    }

    fun speak(
        text: String
    ) {

        voiceState = VoiceState.SPEAKING

        textToSpeechManager.speak(text)
    }

    fun stopSpeaking() {

        textToSpeechManager.stop()

        voiceState = VoiceState.IDLE
    }

    fun stopListening() {

        speechToTextManager.destroy()

        voiceState = VoiceState.IDLE
    }

    fun shutdown() {

        speechToTextManager.destroy()

        textToSpeechManager.shutdown()

        voiceState = VoiceState.IDLE
    }
}
