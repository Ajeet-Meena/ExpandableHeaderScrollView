# ExpandableHeaderScrollView
A tinder like expandable header scroll view.

Create scroll view with ExpandableHeaderScrollView and wrap Header inside linear layout in ExpandableHeaderScrollView.

```xml
<?xml version="1.0" encoding="utf-8"?>
<ExpandableHeaderScrollView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/scroll_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white_dark"
    android:fillViewport="true">

    <LinearLayout
        android:id="@+id/linear_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <ImageView
            android:id="@+id/header"
            android:layout_width="match_parent"
            android:layout_height="240dp"
            android:src="@drawable/background_resource"
            />

        <View
            android:layout_width="match_parent"
            android:layout_height="800dp"
            android:layout_margin="32dp"
            android:background="#234"/>
    </LinearLayout>

</ExpandableHeaderScrollView>

```

Inside Activity, initialize and add header to ExpandableHeaderScrollView.

```java
ExpandableHeaderScrollView scrollView = (ExpandableHeaderScrollView) findViewById(R.id.scroll_view);
LinearLayout parentLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
ImageView headerImageView = (ImageView) findViewById(R.id.header);
scrollView.addHeader(context, parentLinearLayout, headerImageView);
```

Demo on youtube

<a href="http://www.youtube.com/watch?feature=player_embedded&v=DYSqgqZuQQk
" target="_blank"><img src="http://img.youtube.com/vi/DYSqgqZuQQk/0.jpg" 
alt="Demo" width="240" height="180" border="10" /></a>


