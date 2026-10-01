package com.ora.ai.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class VoicePermissionHelper(
    private val context: Context
) {

    fun isMicrophonePermissionGranted(): Boolean {

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
