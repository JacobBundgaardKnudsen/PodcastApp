package com.example.jacob.musictest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    MediaPlayer Song;
    int pause;
    int numberOfSubCasts = 0;

    String inputText;
    String separator = "[";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void play(View view){

        if(Song == null) {
            Song = MediaPlayer.create(this, R.raw.podcast);
            Song.start();
            Toast.makeText(MainActivity.this, "Podcast Started", Toast.LENGTH_SHORT).show();
        }
        else if(!Song.isPlaying()){
            Song.seekTo(pause);
            Song.start();
            Toast.makeText(MainActivity.this, "Podcast Continued", Toast.LENGTH_SHORT).show();
        }
    }

    public void pause(View view){
        if(Song != null){
        Song.pause();
        pause = Song.getCurrentPosition();
            Toast.makeText(MainActivity.this, "Podcast Paused", Toast.LENGTH_SHORT).show();
        }

    }
    public void stop(View view){
        if(Song!=null) {
            Song.stop();
            Song = null;
            Toast.makeText(MainActivity.this, "Podcast Stopped", Toast.LENGTH_SHORT).show();
        }

    }

    public void save(View view){
        if(Song != null){
            //Song.pause();
            pause = Song.getCurrentPosition();

            SharedPreferences sharedPref = getSharedPreferences("Podcast timings", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("currentPos", pause);
            editor.apply();


            try{
                inputText = loadText();
            } catch (IOException e){}

            saveSub(trimText(extractText(getCurrentNumber())),counter());

            Toast.makeText(MainActivity.this, "Podcast saved", Toast.LENGTH_SHORT).show();
            numberOfSubCasts++;

            int tempCounter = counter();
            SharedPreferences sharedPrefCounter = getSharedPreferences("NumberOfSubs", Context.MODE_PRIVATE);
            sharedPrefCounter.edit().clear().commit();

            SharedPreferences.Editor editorC = sharedPrefCounter.edit();
            editorC.putInt("Counter", tempCounter+1);
            editorC.apply();
        }
    }


    public int getCurrentNumber(){
        SharedPreferences sharedPref = getSharedPreferences("Podcast timings", Context.MODE_PRIVATE);
        return sharedPref.getInt("currentPos",100);
    }

    public String loadText() throws IOException{
        InputStream is = this.getResources().openRawResource(R.raw.subs);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine);
            mLine = reader.readLine();
        }
        reader.close();

        return sb.toString();
    }

    public String extractText(int currentNumber){
        int lastSep = 0;
        int highest = 0;
        int start = 0;
        int end = 0;
        int current;

        for(int i = -1; (i = inputText.indexOf(separator, i + 1)) != -1; ){
            current = Integer.parseInt(inputText.substring(i+1,i+7));

            if (current < (currentNumber - 6000)){
                if(i > highest) {
                    highest = i;
                    start = i;
                }
            }
            else if (current > currentNumber && lastSep == 0){
                lastSep = current;
                end = i;
            }
        }
        return inputText.substring(start,end);
    }

    public String trimText(String eText){
        String finalString;

        Pattern p = Pattern.compile("\\[.*?\\]");
        finalString = p.matcher(eText).replaceAll("");

        return finalString;
    }


    public void saveSub(String str, int number){
        SharedPreferences sharedPref = getSharedPreferences("PodCast", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("subPodCast" + number, str);
        editor.apply();
    }

    public void showPodcasts(View view){
        Intent activity = new Intent(this,SubTexts.class);
        startActivity(activity);
    }

    public int counter(){
        SharedPreferences sharedPref = getSharedPreferences("NumberOfSubs", Context.MODE_PRIVATE);
        return sharedPref.getInt("Counter", 0);

    }

}
