[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PinterestView-green.svg?style=flat)](https://android-arsenal.com/details/1/2595)
# Pinterest like awesome menu control for Android

## DEMO
![MINE](./mine1.gif)

#NOTE
 Any pull request is available.
#HOW TO USE
```java

     /**
          * PinterestView'layoutParams must match_parent or fill_parent,
          * just for cover the whole screen
          */
         pinterestView = (PinterestView) findViewById(R.id.item_layout);
         /**
          * add item view into pinterestView
          */
         pinterestView.addShowView(40,createCircleImage(R.drawable.googleplus)
         ,createCircleImage(R.drawable.linkedin),createCircleImage(R.drawable.twitter)
         ,createCircleImage(R.drawable.pinterest));
         /**
          * add pinterestview menu and Pre click view click
          */
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
         /**
          * dispatch pre click view onTouchEvent to PinterestView
          */
         findViewById(R.id.text).setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 pinterestView.dispatchTouchEvent(event);
                 return true;
             }
         });

```

```xml

 <com.brucetoo.pinterestview.PinterestView
        xmlns:custom="http://schemas.android.com/apk/res-auto"
        android:id="@+id/item_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#50000000"
        custom:childSize="44px"
        android:visibility="gone"
        custom:fromDegrees="150.0"
        custom:toDegrees="300.0" />
        
        //Note,this two params don'n need now,it can be auto compute 
        //degrees range by position u click(a little bit coarse)
        custom:fromDegrees="150.0"
        custom:toDegrees="300.0"

```

##  TODO

1. Enhance the item choose animation(More smooth),But how????

2. ~~Add more default Degree Range(Demo use 150° - 300°,just for testing)~~

3. ~~Item Menu add top text~~

4. ~~the point you click to show different type of Degree Range~~

5. ~~Enhance MotionEvent.ACTION_MOVE listen...~~

6. ~~Add branch "RecyclerView-demo" to show how to use with RecyclerView~~

7. PinterestView show more accurately by the position click

## License

Copyright 2015 Bruce too

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

See [LICENSE](LICENSE) file for details.
