package com.brucetoo.pinterestview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Bruce Too
 * On 10/6/15.
 * At 00:01
 * If want to use PinterestView in RecyclerView ScrollView ListView GirdView ect,
 * 1.you need setOnTouchListener about it to handle touch event when PinterestView is visible
 * 2.also need listen itemView setOnTouchListener to dispatch touch event
 * Or if you have better way,let me know please,https://github.com/brucetoo/PinterestView
 */
public class RecyclerFragment extends Fragment {

    private RecyclerView recyclerView;
    private PinterestView pinterestView;

    public static RecyclerFragment getInstance() {
        RecyclerFragment fragment = new RecyclerFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        pinterestView = (PinterestView) view.findViewById(R.id.pin_view);
        pinterestView.addShowView(40, createChildView(R.drawable.googleplus, "")
                , createChildView(R.drawable.linkedin, "linkedin"), createChildView(R.drawable.twitter, "twitter")
                , createChildView(R.drawable.pinterest, "pinterest"));
        pinterestView.setPinClickListener(new PinterestView.PinMenuClickListener() {

            @Override
            public void onMenuItemClick(int childAt) {
                String tips = (String) pinterestView.getChildAt(childAt).getTag();
                Toast.makeText(getActivity(), tips + " clicked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreViewClick() {
                Toast.makeText(getActivity(), "item clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(new RecyclerAdapter());
        /**
         * Note:This is important(but i still don't know why)
         * RecyclerView ScrollView ListView GirdView ect, need setOnTouchListener
         * to judge if PinterestView is visible,if so,let PinterestView handle the
         * touch event,vice versa
         */
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return pinterestView.dispatchTouchEvent(event);
            }
        });
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnTouchListener {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_recycler, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position % 2 == 0) {
                ((ItemViewHolder) holder).imageView.setImageResource(R.drawable.p1);
            } else {
                ((ItemViewHolder) holder).imageView.setImageResource(R.drawable.p2);
            }
            ((ItemViewHolder) holder).imageView.setOnTouchListener(this);
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //long press item view to sho PinterestView
            pinterestView.dispatchTouchEvent(event);
            return true;
        }

    }

    public View createChildView(int imageId, String tip) {
        CircleImageView imageView = new CircleImageView(getActivity());
        imageView.setBorderWidth(0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setFillColor(getResources().getColor(R.color.colorAccent));
        imageView.setImageResource(imageId);
        //just for save Menu item tips
        imageView.setTag(tip);
        return imageView;
    }

}
