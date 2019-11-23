package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity {

    Button btn_next,btn_previous,btn_pause;
    TextView songTextLabel;
    SeekBar songSeekbar;

    static MediaPlayer mediaPlayer;
    int position;
    String s_name;

    ArrayList<File> mySongs;
    Thread updateSeekbar;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);
        songTextLabel = (TextView) findViewById(R.id.txtSongLabel);
        songSeekbar = (SeekBar) findViewById(R.id.seekBar);

        updateSeekbar = new Thread()
        {
            @Override
            public void run() {

                int totalDuration = mediaPlayer.getDuration();
                int currentDuration = 0;
                while (currentDuration<totalDuration)
                {
                    try
                    {
                        sleep(500);
                        currentDuration = mediaPlayer.getCurrentPosition();
                        songSeekbar.setProgress(currentDuration);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mediaPlayer !=null)
        {
            mediaPlayer.pause();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");

        s_name = mySongs.get(position).getName().toString();

        final String songName = i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position = bundle.getInt("pos");

        Uri u = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
        mediaPlayer.start();

        songSeekbar.setMax(mediaPlayer.getDuration());

        updateSeekbar.start();

        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        songSeekbar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),
                PorterDuff.Mode.SRC_IN);

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setMax(mediaPlayer.getDuration());

                if(mediaPlayer.isPlaying())
                {
                    btn_pause.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else
                {
                    btn_pause.setBackgroundResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                position = (position+1)%(mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                s_name = mySongs.get(position).getName();
                songTextLabel.setText(s_name);
                songSeekbar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                btn_pause.setBackgroundResource(R.drawable.pause);

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.pause();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                s_name = mySongs.get(position).getName().toString();
                songTextLabel.setText(s_name);
                songSeekbar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                btn_pause.setBackgroundResource(R.drawable.pause);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}