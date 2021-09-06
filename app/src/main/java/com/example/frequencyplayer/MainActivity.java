package com.example.frequencyplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.FrequencyPlayModule.Audio;

public class MainActivity extends AppCompatActivity  {

    private int granule = 100, start = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Audio audio = new Audio();

        NumberPicker np1 =  findViewById(R.id.picker1);
        np1.setMinValue(0);
        np1.setMaxValue(10);

        Button playBtn = findViewById(R.id.button);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audio !=null && !audio.isPlaying()){
                    audio.start();
                    int id = np1.getValue();
                    audio.frequency = start + id*granule + 50;
                    Log.d("IM LOG", "START : "+id);
                }
            }
        });
        Button stopBtn = findViewById(R.id.button2);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audio !=null && audio.isPlaying()){
                    audio.stop();
                    Log.d("IM LOG", "STOP");
                }
            }
        });
    }
}