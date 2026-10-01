package com.ora.ai.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiApiService {

    suspend fun generateText(
        apiKey: String,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {

        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Gemini API key is missing.")
            )
        }

        if (userMessage.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Message is empty.")
            )
        }

        try {
            val model = "gemini-3.6-flash"

            val url = URL(
                "https://generativelanguage.googleapis.com/v1beta/" +
                    "models/$model:generateContent"
            )

            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty(
                    "x-goog-api-key",
                    apiKey
                )
                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )
                connection.connectTimeout = 15_000
                connection.readTimeout = 30_000
                connection.doOutput = true

                val requestBody = JSONObject()
                    .put(
                        "contents",
                        JSONArray().put(
                            JSONObject()
                                .put(
                                    "parts",
                                    JSONArray().put(
                                        JSONObject()
                                            .put("text", userMessage)
                                    )
                                )
                        )
                    )
                    .toString()

                connection.outputStream.use { output ->
                    output.write(requestBody.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode

                val responseText = if (responseCode in 200..299) {
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }
                } else {
                    connection.errorStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: "Gemini API request failed."
                }

                if (responseCode !in 200..299) {
                    return@withContext Result.failure(
                        IllegalStateException(
                            "Gemini API error ($responseCode): $responseText"
                        )
                    )
                }

                val responseJson = JSONObject(responseText)

                val text = responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                Result.success(text)

            } finally {
                connection.disconnect()
            }

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
