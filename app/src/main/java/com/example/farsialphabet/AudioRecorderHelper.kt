package com.example.farsialphabet

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {
    private var recorder: MediaRecorder? = null
    var isRecording = false
        private set

    fun getCustomRecordingFile(letterId: Int): File {
        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_MUSIC)
        return File(dir, "custom_letter_$letterId.3gp")
    }

    fun startRecording(letterId: Int) {
        val outputFile = getCustomRecordingFile(letterId)
        
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
                isRecording = true
                Log.d("AudioRecorder", "Started recording to ${outputFile.absolutePath}")
            } catch (e: IOException) {
                Log.e("AudioRecorder", "prepare() failed", e)
            } catch (e: IllegalStateException) {
                Log.e("AudioRecorder", "start() failed", e)
            }
        }
    }

    fun stopRecording() {
        if (!isRecording) return
        
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorder", "stop() failed", e)
        } finally {
            recorder = null
            isRecording = false
            Log.d("AudioRecorder", "Stopped recording")
        }
    }
}
