package com.example.jacob.musictest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ListOfPodcasts extends AppCompatActivity {
    MediaPlayer podcastName;
    Button b1;
    Button b2;
    Button b3;
    Button b4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_podcasts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b1 = (Button) findViewById(R.id.podcast1);
        b1.setOnClickListener(myhandler1);

        b2 = (Button) findViewById(R.id.podcast2);
        b2.setOnClickListener(myhandler2);

        b3 = (Button) findViewById(R.id.podcast3);
        b3.setOnClickListener(myhandler3);

        b4 = (Button) findViewById(R.id.podcast4);
        b4.setOnClickListener(myhandler4);

    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View view) {
            //setPodcast("a_young_inventors_plan_to_recycle");
            play("a_young_inventors_plan_to_recycle");
            changeView();
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View view) {
            //setPodcast("podcast2");
            play("the_surprising_science_of_happiness");
            changeView();
        }
    };

    View.OnClickListener myhandler3 = new View.OnClickListener() {
        public void onClick(View view) {
            //setPodcast("podcast3");
            play("the_paradox_of_choice");
            changeView();
        }
    };

    View.OnClickListener myhandler4 = new View.OnClickListener() {
        public void onClick(View view) {
            //setPodcast("podcast4");
            play("life_lessons_from_an_ad_man");
            changeView();
        }
    };

    /*
    public void setPodcast(String podcastName){
        SharedPreferences sharedPrefCounter = getSharedPreferences("Podcasts", Context.MODE_PRIVATE);
        sharedPrefCounter.edit().clear().commit();

        SharedPreferences.Editor editorC = sharedPrefCounter.edit();
        editorC.putString("CurrentPodcast", podcastName);
        editorC.apply();
    }
    */

    public void play(String songName){

        //if(podcastName == null) {

            //Finding the ID of the chosen podcast
            //int songID = getResources().getIdentifier(songName+"_audio","raw",getPackageName());
            //podcastName = MediaPlayer.create(this, songID);

            //Saving the the podcast name for later use
            SharedPreferences sharedPref = getSharedPreferences("PodcastSong", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentSong", songName);
            editor.apply();

            //Starting the podcast
            //podcastName.start();
            Toast.makeText(ListOfPodcasts.this, "Podcast Selected", Toast.LENGTH_SHORT).show();
        //}
    }

    public void changeView(){
        Intent activity = new Intent(this,MainActivity.class);
        startActivity(activity);
    }

}
