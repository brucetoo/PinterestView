package com.brucetoo.pinterestview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

        /**
         * PinterestView'layoutParams must match_parent or fill_parent,
         * just for cover the whole screen
         */
        pinterestView = (PinterestView) findViewById(R.id.item_layout);

        /**
         * add item view into pinterestView
         */
        pinterestView.addMenuItem(createChildView(R.drawable.googleplus,"")
        , createChildView(R.drawable.linkedin,"linkedin"), createChildView(R.drawable.twitter,"twitter")
        , createChildView(R.drawable.pinterest,"pinterest"));
        /**
         * add pinterestview menu and Pre click view click
         */
        pinterestView.setPinClickListener(new PinterestView.PinMenuClickListener() {

            @Override
            public void onMenuItemClick(View view,int clickItemPos) {
                Toast.makeText(MainActivity.this, view.getTag() + " clicked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnchorViewClick() {
                Toast.makeText(MainActivity.this, "button clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * dispatch pre click view onTouchEvent to PinterestView
         */
        findViewById(R.id.recyclerView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.recycler_fragment, RecyclerFragment.getInstance(), "RecyclerFragment")
                            .addToBackStack(null).commit();
                }
                return true;
            }
        });

        findViewById(R.id.left_top).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.top).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.right_top).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });
        findViewById(R.id.left).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.right).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.left_bottom).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });

        findViewById(R.id.bottom).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pinterestView.dispatchTouchEvent(event);
                return true;
            }
        });


        CircleImageView imageView = (CircleImageView) findViewById(R.id.image);
        imageView.setFillColor(getResources().getColor(R.color.colorAccent));
    }

    public View createChildView(int imageId,String tip){
        CircleImageView imageView = new CircleImageView(this);
        imageView.setBorderWidth(0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setFillColor(getResources().getColor(R.color.colorAccent));
        imageView.setImageResource(imageId);
        //just for save Menu item tips
        imageView.setTag(tip);
        return imageView;
    }

    @Override
    public void onBackPressed() {
        int fragments = getFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        }
        super.onBackPressed();
    }
}
