package com.github.projectm_android;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;

public class AudioThread extends Thread {
    private volatile boolean keepRecording = true;

    @Override
    public void run() {
        final int sampleRate = 11025;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
                CHANNEL_IN_MONO,
                ENCODING_PCM_16BIT);
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            Log.e("AudioThread", "AudioRecord returned invalid buffer size: " + bufferSize);
            return;
        }
        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                sampleRate,
                CHANNEL_IN_MONO,
                ENCODING_PCM_16BIT,
                bufferSize);
        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioThread", "Unable to initialize audio recording");
            record.release();
            return;
        }

        short[] audioBuffer = new short[Math.max(1, bufferSize / 2)];
        Log.d("AudioThread", "bufferSize: " + bufferSize);

        try {
            record.startRecording();
            while (keepRecording) {
                int samplesRead = record.read(audioBuffer, 0, audioBuffer.length);
                if (samplesRead > 0) {
                    libprojectMJNIWrapper.addPCM(audioBuffer, (short) samplesRead);
                } else if (samplesRead < 0) {
                    Log.e("AudioThread", "AudioRecord read failed: " + samplesRead);
                    break;
                }
            }
        } catch (SecurityException e) {
            Log.e("AudioThread", "Audio recording permission is unavailable", e);
        } finally {
            if (record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                record.stop();
            }
            record.release();
        }
    }

    public void stopRecording() {
        keepRecording = false;
    }

}
