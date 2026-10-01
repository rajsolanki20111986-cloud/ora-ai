package com.ora.ai.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechManager(
    context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech: TextToSpeech =
        TextToSpeech(context.applicationContext, this)

    private var isReady = false

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {

            val result = textToSpeech.setLanguage(
                Locale("hi", "IN")
            )

            isReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speak(
        text: String
    ) {

        if (!isReady) {
            return
        }

        val cleanText = text.trim()

        if (cleanText.isBlank()) {
            return
        }

        textToSpeech.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "ORA_RESPONSE"
        )
    }

    fun stop() {
        textToSpeech.stop()
    }

    fun shutdown() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        isReady = false
    }
}
