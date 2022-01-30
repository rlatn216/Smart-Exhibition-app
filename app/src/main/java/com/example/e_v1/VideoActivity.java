package com.example.e_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView video_play;
    public static Context video_A;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        video_play = findViewById(R.id.video_play);

        Intent intent = getIntent();

        String file_uri = intent.getExtras().getString("Uri");


        video_play.setVideoURI(Uri.parse(file_uri));


        video_play.setMediaController(new MediaController(this));
        video_play.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작

                video_play.start();

            }
        });


        video_A = this;


    }
}