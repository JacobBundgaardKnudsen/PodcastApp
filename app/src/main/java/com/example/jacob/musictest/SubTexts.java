package com.example.jacob.musictest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SubTexts extends AppCompatActivity {

    TextView subTexts;
    Button b1;

    /*
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_texts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b1 = (Button) findViewById(R.id.reset);
        b1.setOnClickListener(myhandler1);

        subTexts = (TextView) findViewById(R.id.subText);
        subTexts.setMovementMethod(new ScrollingMovementMethod());

        SharedPreferences sharedPref = getSharedPreferences("PodCast", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPref.getAll();




        String boldStr;
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            boldStr = entry.getKey();
            subTexts.append(Html.fromHtml("<b>" + boldStr + ": </b>" + "<small>" +
                    entry.getValue().toString()+ "</small>"));
            subTexts.append("\r\n" + "\r\n");
        }



    }

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            SharedPreferences sharedPref = getSharedPreferences("PodCast", Context.MODE_PRIVATE);
            sharedPref.edit().clear().commit();
            subTexts.setText("");

            SharedPreferences sharedPrefCounter = getSharedPreferences("NumberOfSubs", Context.MODE_PRIVATE);
            sharedPrefCounter.edit().clear().commit();

            SharedPreferences.Editor editor = sharedPrefCounter.edit();
            editor.putInt("Counter", 0);
            editor.apply();
        }

    };


}
