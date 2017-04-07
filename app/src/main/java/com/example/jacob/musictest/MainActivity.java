package com.example.jacob.musictest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.list;


public class MainActivity extends AppCompatActivity {
    int pause;
    int numberOfSubCasts = 0;
    String inputText;
    String separator = ">";
    MediaPlayer Song;

    //Name of song name, edit this if multiple podcasts are added
    String songName = "a_young_inventors_plan_to_recycle";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void play(View view){

        if(Song == null) {

            //Finding the ID of the chosen podcast
            int songID = getResources().getIdentifier(songName+"_audio","raw",getPackageName());
            Song = MediaPlayer.create(this, songID);

            //Saving the the podcast name for later use
            SharedPreferences sharedPref = getSharedPreferences("PodcastSong", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("currentSong", songName);
            editor.apply();

            //Starting the podcast
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
            //Getting the position in the audio where the podcast is paused
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
            pause = Song.getCurrentPosition();

            //Saving the time from where the podcast was saved
            SharedPreferences sharedPref = getSharedPreferences("Podcast timings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("currentPos", pause);
            editor.apply();

            //Reading/loading the subtitles to the podcast
            try{
                inputText = loadText();
            } catch (IOException e){}

            saveSub(trimText(extractText(getCurrentNumber())),counter());
            //saveSub(extractText(30000),2);
            //saveSub(trimText(extractText(300000)),counter());


            Toast.makeText(MainActivity.this, "Bit saved", Toast.LENGTH_SHORT).show();
            numberOfSubCasts++;

            //Increasing / keeping track of the numbers of podcasts
            int tempCounter = counter();
            SharedPreferences sharedPrefCounter = getSharedPreferences("NumberOfSubs", Context.MODE_PRIVATE);
            sharedPrefCounter.edit().clear().commit();

            SharedPreferences.Editor editorC = sharedPrefCounter.edit();
            editorC.putInt("Counter", tempCounter+1);
            editorC.apply();
        }
    }

    //Retrieving the number at which the podcast was last paused
    public int getCurrentNumber(){
        SharedPreferences sharedPref = getSharedPreferences("Podcast timings", Context.MODE_PRIVATE);
        return sharedPref.getInt("currentPos",100);
    }

    public String loadText() throws IOException{
        int subID = getResources().getIdentifier(songName,"raw",getPackageName());

        //Finding the ID of the subtitles
        InputStream is = getResources().openRawResource(getResources().getIdentifier(songName,"raw", getPackageName()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();

        //Looping through the subtitles and appending it to a string
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
        String finalString;
        String content = "";

        List<String> startList = new ArrayList<>();
        List<String> endList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();

        //Patterns used to categorize the different parts in the text
        String lineNumberPattern = "(\\d+\\s)";
        String timeStampPattern = "([\\d:,]+)";
        String contentPattern = "(.*)";

        Matcher matcher = Pattern.compile(lineNumberPattern + timeStampPattern + "( --> )" + timeStampPattern + "(\\s)" + contentPattern).matcher(eText);

        //Adding different patterns into start time, end time and content
        while(matcher.find()) {
            startList.add(matcher.group(2));
            endList.add(matcher.group(4));
            contentList.add(matcher.group(6));
        }

        //Creating a single string containing all content
        for(int i = 0; i < contentList.size(); i++){
            content = content + contentList.get(i);
        }

        //Patterns of the two types of subtitle syntax (Linenumber, timestamp, -->, timestamp) and
        //                                             (LineNumber, timestamp, -->, timestamp, space)
        Pattern patternWithSpacing = Pattern.compile(lineNumberPattern + timeStampPattern + "( --> )" + timeStampPattern + "(\\s)");
        Pattern patternWithoutSpacing = Pattern.compile(lineNumberPattern + timeStampPattern + "( --> )" + timeStampPattern);

        //Removing/replacing the above patterns from the given text
        tfinalString = patternWithSpacing.matcher(content).replaceAll("");
        finalString = patternWithoutSpacing.matcher(tfinalString).replaceAll("");

        //Returning the start time and end time followed by the content
        return startList.get(0).substring(3,8) + " --> " + endList.get(endList.size()-1).substring(3,8) + "\n" + finalString;

        /*
        return startList.get(0) + " --> " + endList.get(endList.size()-1) + "\n" + finalString;
        */
    }


    public void saveSub(String str, int number){
        String headline;

        //Retrieving the name of the podcast
        SharedPreferences sharedPref = getSharedPreferences("PodcastSong", Context.MODE_PRIVATE);
        String podCastName = sharedPref.getString("currentSong","");

        //Renaming the podcast name by removing "_" with " " and also adding a number to the podcast
        //corresponding to the current number of podcasts saved
        Pattern p = Pattern.compile("(_)");
        headline = p.matcher(podCastName).replaceAll(" ") + " " + number;

        //Saving the new name along with corresponding subtitles
        SharedPreferences SP = getSharedPreferences("Podcast",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = SP.edit();
        editor.putString(headline, str);
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

    //Converts the timing syntax of TedTalk to milliseconds
    public int convertToMilliseconds (int hour, int minutes, int seconds) {
        return (hour * 360 + minutes * 60 + seconds) * 1000;
    }
}
