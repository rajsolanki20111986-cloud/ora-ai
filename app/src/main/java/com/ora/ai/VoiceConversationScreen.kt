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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ora.ai.voice.VoiceConversationViewModel
import com.ora.ai.voice.VoiceState

@Composable
fun VoiceConversationScreen(
    onEndCall: () -> Unit,
    onRecognizedText: (String) -> Unit = {},
    voiceViewModel: VoiceConversationViewModel = viewModel()
) {

    val state by voiceViewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101820))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(70.dp)
        )

        Text(
            text = "ORA",
            color = Color.White,
            fontSize = 32.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = when (state.voiceState) {
                VoiceState.LISTENING -> "Listening..."
                VoiceState.PROCESSING -> "Processing..."
                VoiceState.SPEAKING -> "Speaking..."
                VoiceState.ERROR -> "Voice Error"
                VoiceState.IDLE -> "Voice Conversation"
            },
            color = when (state.voiceState) {
                VoiceState.ERROR -> Color(0xFFFF9E9E)
                VoiceState.LISTENING -> Color(0xFF8FE3A8)
                VoiceState.SPEAKING -> Color(0xFFE8D58A)
                else -> Color(0xFFB8C5CC)
            },
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    when (state.voiceState) {
                        VoiceState.LISTENING ->
                            Color(0x554F9B78)

                        VoiceState.SPEAKING ->
                            Color(0x556E8FA3)

                        else ->
                            Color(0x332A3A46)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6E8FA3)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "ORA",
                    color = Color.White,
                    fontSize = 30.sp
                )
            }
        }

        if (state.errorMessage.isNotBlank()) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = state.errorMessage,
                color = Color(0xFFFFB4B4),
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {

                    if (state.voiceState == VoiceState.LISTENING) {
                        voiceViewModel.stopListening()
                    } else {
                        voiceViewModel.startListening(
                            onTextResult = { text ->
                                onRecognizedText(text)
                            }
                        )
                    }
                }
            ) {

                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color(0x332A3A46)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (
                            state.voiceState == VoiceState.LISTENING
                        ) {
                            Icons.Default.MicOff
                        } else {
                            Icons.Default.Mic
                        },
                        contentDescription = "Microphone",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.size(28.dp)
            )

            IconButton(
                onClick = onEndCall
            ) {

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B4545)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End voice conversation",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}
