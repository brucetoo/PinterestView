# Pinterest like awesome menu control for Android
# NOTE
  Because the narrow range of my knowledge,Any Pull Requests are welcome.
  Or u have better way to work this out,feedback me [@Brucetoo](https://twitter.com/Brucetoo14),Thank u.
## Pinterest like 
![Pinterest](./pinterest.gif)

## This lib like 
![MINE](./mine.gif)


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

##  TODO

1. Enhance the item choose animation(More smooth)
2. Add more default Degree Range(Demo use 150° - 300°,just for testing)
3. Item Menu add top text like Pinterest
4. Thinking...

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
