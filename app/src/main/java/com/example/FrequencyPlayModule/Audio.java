package com.example.FrequencyPlayModule;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

// Method
// 1. Start / Stop
// 2. Set Attributes : frequency, level, waveform, mute

public class Audio implements Runnable {
    protected static final int SINE = 0;
    protected static final int SQUARE = 1;
    protected static final int SAWTOOTH = 2;

    public int waveform;
    public boolean mute;
    public double frequency; // 0.1~25000 (0.1Hz - 25KHz)
    public double level; // 0~1 (-80~0dB)

    private boolean isPlaying;

    protected Thread thread;

    private AudioTrack audioTrack;

    public Audio() {
        frequency = 440.0;
        level = 1;
        waveform = SINE;
        isPlaying = false;
    }

    public boolean isPlaying(){return this.isPlaying;}
    // Start
    public void start() {
        isPlaying = true;
        thread = new Thread(this, "Audio");
        thread.start();
    }

    // Stop
    public void stop() {
        isPlaying = false;
        Thread t = thread;
        thread = null;

        // Wait for the thread to exit
        while (t != null && t.isAlive())
            Thread.yield();
    }

    public void run() {
        processAudio();
    }

    // Process audio
    @SuppressWarnings("deprecation")
    private void processAudio() {
        short buffer[];

        int rate =
                AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
        int minSize =
                AudioTrack.getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

        // Find a suitable buffer size
        int sizes[] = {1024, 2048, 4096, 8192, 16384, 32768};
        int size = 0;

        for (int s : sizes) {
            if (s > minSize) {
                size = s;
                break;
            }
        }

        final double K = 2.0 * Math.PI / rate;

        // Create the audio track
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, rate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                size, AudioTrack.MODE_STREAM);
        // Check audioTrack

        // Check state
        int state = audioTrack.getState();

        if (state != AudioTrack.STATE_INITIALIZED) {
            audioTrack.release();
            return;
        }

        audioTrack.play();

        // Create the buffer
        buffer = new short[size];

        // Initialise the generator variables
        double f = frequency;
        double l = 0.0;
        double q = 0.0;

        while (thread != null) {
            // Fill the current buffer
            for (int i = 0; i < buffer.length; i++) {
                f += (frequency - f) / 4096.0;
                l += ((mute ? 0.0 : level) * 16384.0 - l) / 4096.0;
                q += (q < Math.PI) ? f * K : (f * K) - (2.0 * Math.PI);

                switch (waveform) {
                    case SINE:
                        buffer[i] = (short) Math.round(Math.sin(q) * l);
                        break;

                    case SQUARE:
                        buffer[i] = (short) ((q > 0.0) ? l : -l);
                        break;

                    case SAWTOOTH:
                        buffer[i] = (short) Math.round((q / Math.PI) * l);
                        break;
                }
            }

            audioTrack.write(buffer, 0, buffer.length);
        }

        audioTrack.stop();
        audioTrack.release();
    }
}