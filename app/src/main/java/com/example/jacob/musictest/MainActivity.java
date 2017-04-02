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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    MediaPlayer Song;
    int pause;
    int numberOfSubCasts = 0;
    String songName = "a_young_inventors_plan_to_recycle";

    String inputText;
    String separator = ">";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void play(View view){

        if(Song == null) {

            int songID = getResources().getIdentifier(songName+"audio","raw",getPackageName());
            Song = MediaPlayer.create(this, songID);

            SharedPreferences sharedPref = getSharedPreferences("PodCastSong", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentSong", songName);
            editor.apply();


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

            //saveSub(trimText(extractText(getCurrentNumber())),counter());
            //saveSub(extractText(30000),2);
            saveSub(trimText(extractText(300000)),counter());


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
        int subID = getResources().getIdentifier(songName,"raw",getPackageName());


        //InputStream is = this.getResources().openRawResource(R.raw.a_young_inventors_plan_to_recycle);
        InputStream is = getResources().openRawResource(getResources().getIdentifier(songName,"raw", getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine);
            sb.append(" ");
            mLine = reader.readLine();
        }
        reader.close();

        return sb.toString();
    }

    public String extractText(int currentNumber){
        int lastSep = 0; int highest = 0; int start = 0; int end = 0;
        int fcurrent, lcurrent;

        //Looping through the subtitles
        for(int i = -1; (i = inputText.indexOf(separator, i + 1)) != -1; ){

            //Converting HH:MM:SS into milliseconds
            fcurrent = convertToMilliseconds(Integer.parseInt(inputText.substring(i-15,i-13)),  //Hours
                    Integer.parseInt(inputText.substring(i-12,i-10)),                           //Minutes
                    Integer.parseInt(inputText.substring(i-9,i-7)));                            //Seconds


            lcurrent = convertToMilliseconds(Integer.parseInt(inputText.substring(i+2,i+4)),    //Hours
                    Integer.parseInt(inputText.substring(i+5,i+7)),                             //Minutes
                    Integer.parseInt(inputText.substring(i+8,i+10)));                           //Seconds

            //Finding the sentence which were spoken six seconds earlier when the user pressed save
            if (fcurrent < (currentNumber - 6000)){
                if(i > highest) {
                    highest = i;
                    start = i-17;
                }
            }
            //Finding the sentence that were spoken when the user pressed save
            else if (lcurrent > currentNumber && lastSep == 0){
                lastSep = lcurrent;
                end = i+10;
            }
        }
        return inputText.substring(start,end);

    }

    public String trimText(String eText){
        String tfinalString;


        String lineNumberPattern = "(\\d+\\s)";
        String timeStampPattern = "([\\d:,]+)";

        //Patterns of the two types of subtitle syntax (Linenumber, timestamp, -->, timestamp) and
        //                                             (LineNumber, timestamp, -->, timestamp, space)
        Pattern patwithspacing = Pattern.compile(lineNumberPattern + timeStampPattern + "( --> )" + timeStampPattern + "(\\s)");
        Pattern patwithoutspacing = Pattern.compile(lineNumberPattern + timeStampPattern + "( --> )" + timeStampPattern);

        //Removing/replacing the above patterns from the given text
        tfinalString = patwithspacing.matcher(eText).replaceAll("");
        return patwithoutspacing.matcher(tfinalString).replaceAll("");
    }


    public void saveSub(String str, int number){
        SharedPreferences sharedPref = getSharedPreferences("PodCastSong", Context.MODE_PRIVATE);
        String podCastName = sharedPref.getString("currentSong","");

        Pattern p = Pattern.compile("(_)");


        SharedPreferences SP = getSharedPreferences("PodCast",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(p.matcher(podCastName).replaceAll(" ") + " " + number, str);
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
    public int convertToMilliseconds (int hour, int minutes, int seconds) {
        return (hour * 360 + minutes * 60 + seconds) * 1000;
    }

}
