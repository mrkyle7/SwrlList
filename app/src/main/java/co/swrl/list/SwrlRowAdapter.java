package co.swrl.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;


class SwrlRowAdapter extends ArrayAdapter<Swrl> {
    private final ArrayList<Swrl> swrls;
    private final CollectionManager collectionManager;

    SwrlRowAdapter(Context context, int resource, ArrayList<Swrl> swrls, CollectionManager collectionManager) {
        super(context, resource, swrls);
        this.swrls = swrls;
        this.collectionManager = collectionManager;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, null);
        }

        Swrl swrl = swrls.get(position);
        if (swrl != null){
            setTitle(row, swrl);
            setDeleteButton(row, swrl);
        }

        return row;
    }

    private void setDeleteButton(View row, final Swrl swrl) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swrls.remove(swrl);
                collectionManager.markAsDeleted(swrl);
                notifyDataSetChanged();
            }
        });

    }

    private void setTitle(View row, Swrl swrl) {
        TextView title = (TextView) row.findViewById(R.id.list_title);
        title.setText(swrl.getTitle());
    }
}
