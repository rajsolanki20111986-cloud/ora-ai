package com.ora.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ora.ai.data.ApiKeyStorage
import com.ora.ai.viewmodel.ChatViewModel
import com.ora.ai.voice.VoiceConversationViewModel

@Composable
fun ORAScreen(
    chatViewModel: ChatViewModel = viewModel(),
    voiceViewModel: VoiceConversationViewModel = viewModel()
) {

    val context = LocalContext.current

    val apiKeyStorage = remember {
        ApiKeyStorage(context.applicationContext)
    }

    var showSettings by remember {
        mutableStateOf(false)
    }

    var showVoiceConversation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        chatViewModel.setApiKey(
            apiKeyStorage.getApiKey()
        )
    }

    if (showVoiceConversation) {

        VoiceConversationScreen(
            onEndCall = {
                showVoiceConversation = false
            },

            onRecognizedText = { text ->
                chatViewModel.updateMessageText(text)
                chatViewModel.sendMessage()
            },

            voiceViewModel = voiceViewModel
        )

        return
    }

    if (showSettings) {

        SettingsScreen(
            currentApiKey = apiKeyStorage.getApiKey(),

            onSaveApiKey = { key ->
                apiKeyStorage.saveApiKey(key)
                chatViewModel.setApiKey(key)
                showSettings = false
            },

            onClearApiKey = {
                apiKeyStorage.clearApiKey()
                chatViewModel.setApiKey("")
            },

            onBack = {
                showSettings = false
            }
        )

        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "ORA",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = if (chatViewModel.isSending) {
                        "Thinking..."
                    } else {
                        "Online"
                    },
                    color = if (chatViewModel.isSending) {
                        Color(0xFFE8D58A)
                    } else {
                        Color(0xFF8FE3A8)
                    },
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = {
                    showSettings = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            chatViewModel.messages.forEach { message ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isUser) {
                        Arrangement.End
                    } else {
                        Arrangement.Start
                    }
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.86f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 18.dp,
                                    topEnd = 18.dp,
                                    bottomEnd = if (message.isUser) {
                                        4.dp
                                    } else {
                                        18.dp
                                    },
                                    bottomStart = if (message.isUser) {
                                        18.dp
                                    } else {
                                        4.dp
                                    }
                                )
                            )
                            .background(
                                when {
                                    message.isError -> Color(0xFF59383B)
                                    message.isUser -> Color(0xFF405866)
                                    else -> Color(0x332A3A46)
                                }
                            )
                            .padding(15.dp)
                    ) {

                        Column {

                            Text(
                                text = message.text,
                                color = if (message.isError) {
                                    Color(0xFFFFD4D4)
                                } else {
                                    Color.White
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (!message.isUser && !message.isLoading) {

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Text(
                                    text = "👍  👎  🔊  📋  💾  ⋮",
                                    color = Color(0xFFB8C5CC),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 12.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(28.dp)
                    )
                    .background(Color(0x332A3A46))
                    .padding(
                        horizontal = 6.dp,
                        vertical = 2.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = { }
                    ) {
                        Text(
                            text = "📎",
                            fontSize = 19.sp
                        )
                    }

                    TextField(
                        value = chatViewModel.messageText,
                        onValueChange = {
                            chatViewModel.updateMessageText(it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Type a message...",
                                color = Color(0xFF9BAAB3)
                            )
                        },
                        singleLine = true,
                        enabled = !chatViewModel.isSending,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color(0xFF89959C)
                        )
                    )

                    IconButton(
                        onClick = {
                            voiceViewModel.startListening(
                                onTextResult = { text ->
                                    chatViewModel.updateMessageText(text)
                                    chatViewModel.sendMessage()
                                }
                            )
                        },
                        enabled = !chatViewModel.isSending
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice input",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.width(7.dp)
            )

            IconButton(
                onClick = {
                    showVoiceConversation = true
                },
                enabled = !chatViewModel.isSending
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Voice conversation",
                    tint = Color.White
                )
            }

            Spacer(
                modifier = Modifier.width(2.dp)
            )

            IconButton(
                onClick = {
                    chatViewModel.sendMessage()
                },
                enabled = !chatViewModel.isSending
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (chatViewModel.isSending) {
                                Color(0xFF45535B)
                            } else {
                                Color(0xFF6E8FA3)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
