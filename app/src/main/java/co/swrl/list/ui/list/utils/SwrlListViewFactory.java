package co.swrl.list.ui.list.utils;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SwrlListViewFactory {
    private SwrlListViewFactory(){}

    public static RecyclerView setUpListView(Activity activity, RecyclerView listView, RecyclerView.Adapter adapter){
        listView.setLayoutManager(new LinearLayoutManager(activity));
        listView.setAdapter(adapter);
        listView.setHasFixedSize(true);
        listView.setItemViewCacheSize(100);
        listView.setDrawingCacheEnabled(true);
        listView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        return listView;
    }
}
