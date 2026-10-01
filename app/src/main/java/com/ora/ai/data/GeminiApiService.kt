package com.ora.ai.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiApiService {

    companion object {
        private const val MODEL = "gemini-3.8-flash"

        private const val BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/"

        private const val CONNECT_TIMEOUT = 15_000
        private const val READ_TIMEOUT = 45_000
    }

    suspend fun generateText(
        apiKey: String,
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {

        val cleanApiKey = apiKey.trim()
        val cleanMessage = userMessage.trim()

        if (cleanApiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException(
                    "Gemini API key is missing."
                )
            )
        }

        if (cleanMessage.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException(
                    "Message is empty."
                )
            )
        }

        val url = URL(
            "$BASE_URL$MODEL:generateContent"
        )

        var connection: HttpURLConnection? = null

        try {
            connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"

            connection.setRequestProperty(
                "x-goog-api-key",
                cleanApiKey
            )

            connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8"
            )

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            connection.connectTimeout = CONNECT_TIMEOUT
            connection.readTimeout = READ_TIMEOUT
            connection.doOutput = true
            connection.doInput = true

            val requestBody = JSONObject()
                .put(
                    "contents",
                    JSONArray().put(
                        JSONObject()
                            .put(
                                "role",
                                "user"
                            )
                            .put(
                                "parts",
                                JSONArray().put(
                                    JSONObject()
                                        .put(
                                            "text",
                                            cleanMessage
                                        )
                                )
                            )
                    )
                )
                .toString()

            connection.outputStream.use { output ->
                output.write(
                    requestBody.toByteArray(
                        Charsets.UTF_8
                    )
                )
                output.flush()
            }

            val responseCode = connection.responseCode

            val responseText =
                if (responseCode in 200..299) {
                    connection.inputStream
                        .bufferedReader(Charsets.UTF_8)
                        .use { it.readText() }
                } else {
                    connection.errorStream
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: ""
                }

            if (responseCode !in 200..299) {

                val readableError =
                    extractApiError(responseText)

                return@withContext Result.failure(
                    IllegalStateException(
                        "Gemini API error ($responseCode): $readableError"
                    )
                )
            }

            val responseJson =
                JSONObject(responseText)

            val candidates =
                responseJson.optJSONArray("candidates")

            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Gemini returned no response."
                    )
                )
            }

            val firstCandidate =
                candidates.optJSONObject(0)

            val content =
                firstCandidate?.optJSONObject("content")

            val parts =
                content?.optJSONArray("parts")

            if (parts == null || parts.length() == 0) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Gemini returned an empty response."
                    )
                )
            }

            val responseTextValue =
                parts.optJSONObject(0)
                    ?.optString("text", "")
                    ?.trim()
                    ?: ""

            if (responseTextValue.isBlank()) {
                return@withContext Result.failure(
                    IllegalStateException(
                        "Gemini returned empty text."
                    )
                )
            }

            Result.success(responseTextValue)

        } catch (exception: java.net.SocketTimeoutException) {

            Result.failure(
                IllegalStateException(
                    "Gemini request timed out. Please try again.",
                    exception
                )
            )

        } catch (exception: java.net.UnknownHostException) {

            Result.failure(
                IllegalStateException(
                    "Internet connection is unavailable.",
                    exception
                )
            )

        } catch (exception: Exception) {

            Result.failure(
                IllegalStateException(
                    exception.message
                        ?: "Unable to connect to Gemini.",
                    exception
                )
            )

        } finally {
            connection?.disconnect()
        }
    }

    private fun extractApiError(
        responseText: String
    ): String {

        if (responseText.isBlank()) {
            return "Request was rejected by Gemini."
        }

        return try {

            val json =
                JSONObject(responseText)

            val error =
                json.optJSONObject("error")

            val message =
                error?.optString("message", "")

            if (!message.isNullOrBlank()) {
                message
            } else {
                responseText
            }

        } catch (_: Exception) {

            responseText
        }
    }
}
