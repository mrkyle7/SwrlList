package co.swrl.swrllist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class SwrlRowAdapter extends ArrayAdapter<Swrl> {
    private ArrayList<Swrl> swrls;

    SwrlRowAdapter(Context context, int resource, ArrayList<Swrl> swrls) {
        super(context, resource, swrls);
        this.swrls = swrls;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, null);
        }

        Swrl swrl = swrls.get(position);
        if (swrl != null){
            TextView title = (TextView) row.findViewById(R.id.list_item);
            title.setText(swrl.getTitle());
        }

        return row;
    }
}
