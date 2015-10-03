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
import android.widget.Toast;

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
        pinterestView.addShowView(40,createCircleImage(R.drawable.googleplus)
        ,createCircleImage(R.drawable.linkedin),createCircleImage(R.drawable.twitter)
        ,createCircleImage(R.drawable.pinterest));
        pinterestView.setPinClickListener(new PinterestView.PinMenuClickListener() {

            @Override
            public void onMenuItemClick(int childAt) {
                Toast.makeText(MainActivity.this, "onMenuItemClick" + childAt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreViewClick() {
                Toast.makeText(MainActivity.this, "onPreViewClick", Toast.LENGTH_SHORT).show();
            }
        });
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
