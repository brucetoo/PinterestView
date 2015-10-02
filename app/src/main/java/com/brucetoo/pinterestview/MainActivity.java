package com.brucetoo.pinterestview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    PinterestView pinterestView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        pinterestView = (PinterestView) findViewById(R.id.item_layout);
        ImageView image = new ImageView(this);
        image.setBackgroundResource(R.drawable.googleplus);
        pinterestView.addView(createCircleImage(R.drawable.googleplus), 0);
        for (int i = 0; i < 3; i++){
            pinterestView.addView(createCircleImage(R.drawable.twitter));
            pinterestView.setChildSize(40);
        }

        findViewById(R.id.text).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(getClass().getName(), "v.getX()-----" + v.getX());
                Log.i(getClass().getName(), "v.getY()-----" + v.getY());
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        CircleImageView imageView = (CircleImageView) findViewById(R.id.image);
        imageView.setFillColor(getResources().getColor(R.color.colorAccent));

    }

    public CircleImageView createCircleImage(int imageId){
        CircleImageView imageView = new CircleImageView(this);
        imageView.setBorderWidth(0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setFillColor(getResources().getColor(R.color.colorAccent));
        imageView.setImageResource(imageId);
        return imageView;
    }

}
