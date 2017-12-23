package co.swrl.list.ui.list.menus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.swrl.list.R;
import co.swrl.list.item.Type;
import co.swrl.list.ui.activity.ListActivity;

public class DrawerListAdapter extends BaseAdapter {

    private ListActivity listActivity;
    private final Context mContext;
    private final Type[] mNavItems;
    private final Type typeFilter;

    public DrawerListAdapter(ListActivity listActivity, Context context, Type[] navItems, Type typeFilter) {
        this.listActivity = listActivity;
        this.mContext = context;
        this.mNavItems = navItems;
        this.typeFilter = typeFilter;
    }

    @Override
    public int getCount() {
        return mNavItems.length;
    }

    @Override
    public Object getItem(int i) {
        return mNavItems[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        } else {
            view = convertView;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        Type navItem = mNavItems[position];
        if ((typeFilter == null && navItem == Type.UNKNOWN) || navItem == typeFilter) {
            //noinspection deprecation
            view.setBackgroundColor(listActivity.getResources().getColor(R.color.rowHighlight));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
        ImageView border = (ImageView) view.findViewById(R.id.nav_left_border);
        //noinspection deprecation
        border.setBackgroundColor(listActivity.getApplicationContext().getResources().getColor(navItem.getColor()));
        iconView.setImageResource(navItem.getIcon());
        String filterTitle = navItem.getFriendlyNamePlural()
                + " ("
                + (navItem == Type.UNKNOWN ? listActivity.swrlListAdapter.getSwrlCount() : listActivity.swrlListAdapter.getSwrlCount(navItem))
                + ")";
        titleView.setText(filterTitle);
        return view;
    }
}
