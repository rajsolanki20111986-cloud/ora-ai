package com.ora.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    onBack: () -> Unit
) {

    var apiKey by remember {
        mutableStateOf(currentApiKey)
    }

    var showApiKey by remember {
        mutableStateOf(false)
    }

    var showEmptyError by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "Back",
                    color = Color.White
                )
            }

            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 22.sp
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = "Gemini API Key",
            color = Color.White,
            fontSize = 17.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = {
                apiKey = it
                showEmptyError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("API Key")
            },
            placeholder = {
                Text("Paste your Gemini API key")
            },
            singleLine = true,
            isError = showEmptyError,
            visualTransformation = if (showApiKey) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {

                IconButton(
                    onClick = {
                        showApiKey = !showApiKey
                    }
                ) {
                    Icon(
                        imageVector = if (showApiKey) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (showApiKey) {
                            "Hide API key"
                        } else {
                            "Show API key"
                        },
                        tint = Color.White
                    )
                }
            }
        )

        if (showEmptyError) {

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "API key खाली नहीं हो सकती।",
                color = Color(0xFFFF9E9E),
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = {

                val cleanKey = apiKey.trim()

                if (cleanKey.isBlank()) {
                    showEmptyError = true
                } else {
                    onSaveApiKey(cleanKey)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save API Key")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        TextButton(
            onClick = {
                apiKey = ""
                showApiKey = false
                showEmptyError = false
                onClearApiKey()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Clear API Key",
                color = Color.White
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "API key इस device पर local storage में रखी जाती है। इसे GitHub code में नहीं रखा गया है।",
            color = Color(0xFFB8C5CC),
            fontSize = 13.sp
        )
    }
}
