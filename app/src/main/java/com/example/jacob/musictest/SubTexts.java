package com.example.jacob.musictest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Map;

public class SubTexts extends AppCompatActivity {

    TextView subTexts;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_texts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Finding the reset button from the layout and adding a function which is executed whenever the button is pressed
        b1 = (Button) findViewById(R.id.reset);
        b1.setOnClickListener(myhandler1);

        //Finding the textView and makes it scrollable
        subTexts = (TextView) findViewById(R.id.subText);
        subTexts.setMovementMethod(new ScrollingMovementMethod());

        //Loading all items saved in "podcast"
        SharedPreferences sharedPref = getSharedPreferences("Podcast", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPref.getAll();

        String tempTitle;
        String title;
        String newTitle ="";

        //Looping through all the items saved in podcast and appends them into a string
        for (Map.Entry<String, ?> entry : keys.entrySet()) {

            tempTitle = entry.getKey();
            title = tempTitle.substring(0,tempTitle.length()-2);

            if(!title.equals(newTitle)) {
                subTexts.append(Html.fromHtml("<b>" + title + ": </b>"));
            }
            newTitle = title;

            subTexts.append(Html.fromHtml("<small>" +
                    entry.getValue().toString()+ "</small>"));

            /*
            subTexts.append(Html.fromHtml("<b>" + title + ": </b>" + "<small>" +
                    entry.getValue().toString()+ "</small>"));
            */
            subTexts.append("\r\n" + "\r\n");
        }
    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            //Removes all text saved in "podcast" and removes the text in SUBPODCASTS
            SharedPreferences sharedPref = getSharedPreferences("Podcast", Context.MODE_PRIVATE);
            sharedPref.edit().clear().commit();
            subTexts.setText("");

            //Resets the counter that keeps track of how many sub podcasts that have been saved
            SharedPreferences sharedPrefCounter = getSharedPreferences("NumberOfSubs", Context.MODE_PRIVATE);
            sharedPrefCounter.edit().clear().commit();

            SharedPreferences.Editor editor = sharedPrefCounter.edit();
            editor.putInt("Counter", 0);
            editor.apply();
        }
    };
}
