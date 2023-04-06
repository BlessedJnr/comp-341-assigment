package com.example.productivityapp;



import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPag;
    LinearLayout linearLayout;
    Button skip,getStarted;

    TextView[] dots;
    ViewPageAdapter viewPageAdapter;
    boolean isFirstLaunch = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       getStarted = findViewById(R.id.getStarted);
        getStarted.setVisibility(View.GONE);
       // next = findViewById(R.id.next);
        skip = findViewById(R.id.skip);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirstLaunch = ((SharedPreferences) sharedPreferences).getBoolean("isFirstLaunch", true);
        if (isFirstLaunch) {
            // Show your pop-up here
            // ...

            // Set "isFirstLaunch" to false
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }



        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(i);
                finish();
            }
        });
        /*
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getItem(0)< 2){
                    viewPag.setCurrentItem((1), true);
                }
                else{
                    Intent i = new Intent(MainActivity.this, HomeScreen.class);
                    startActivity(i);
                    finish();
                }
            }
        });

 */


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HomeScreen.class);
                startActivity(i);
                finish();
            }
        });
        viewPag = (ViewPager) findViewById(R.id.slideVIEW);
        linearLayout = (LinearLayout) findViewById(R.id.indicator_layout);
        viewPageAdapter = new ViewPageAdapter(this);
        viewPag.setAdapter(viewPageAdapter);
        setIndicatotr(0);
        viewPag.addOnPageChangeListener(viewListener);
    }
    public void setIndicatotr(int position){
        dots= new TextView[3];
        linearLayout.removeAllViews();
        for (int i  = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
            }
            linearLayout.addView(dots[i]);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setIndicatotr(position);
            if(position == 2 ){
              getStarted.setVisibility(View.VISIBLE);

            }
            else{
             getStarted.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private int getItem(int i){

        return viewPag.getCurrentItem() + i;
    }

}