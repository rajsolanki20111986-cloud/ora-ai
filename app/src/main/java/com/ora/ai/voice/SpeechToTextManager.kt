package com.ora.ai.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechToTextManager(
    private val context: Context
) {

    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition इस device पर available नहीं है।")
            return
        }

        speechRecognizer?.destroy()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val listener = object : android.speech.RecognitionListener {

            override fun onResults(results: android.os.Bundle?) {

                val matches =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                val text = matches
                    ?.firstOrNull()
                    ?.trim()
                    ?: ""

                if (text.isBlank()) {
                    onError("कुछ सुनाई नहीं दिया।")
                } else {
                    onResult(text)
                }
            }

            override fun onError(error: Int) {
                onError(
                    when (error) {
                        SpeechRecognizer.ERROR_AUDIO ->
                            "Microphone में समस्या हुई।"

                        SpeechRecognizer.ERROR_NETWORK ->
                            "Speech recognition के लिए network समस्या हुई।"

                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "Speech recognition timeout हो गया।"

                        SpeechRecognizer.ERROR_NO_MATCH ->
                            "कुछ समझ नहीं आया। फिर से बोलें।"

                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                            "आपकी आवाज़ सुनाई नहीं दी।"

                        else ->
                            "Voice input में समस्या हुई।"
                    }
                )
            }

            override fun onReadyForSpeech(params: android.os.Bundle?) = Unit

            override fun onBeginningOfSpeech() = Unit

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray?) = Unit

            override fun onEndOfSpeech() = Unit

            override fun onPartialResults(
                partialResults: android.os.Bundle?
            ) = Unit

            override fun onEvent(
                eventType: Int,
                params: android.os.Bundle?
            ) = Unit
        }

        speechRecognizer?.setRecognitionListener(listener)

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "hi-IN"
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "hi-IN"
            )

            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                1
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                false
            )
        }

        speechRecognizer?.startListening(intent)
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
