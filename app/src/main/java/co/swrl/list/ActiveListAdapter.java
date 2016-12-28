package co.swrl.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivity;
import static co.swrl.list.ViewActivity.EXTRAS_INDEX;
import static co.swrl.list.ViewActivity.EXTRAS_SWRLS;


class ActiveListAdapter extends ArrayAdapter<Swrl> {
    private final ArrayList<Swrl> swrls;
    private final CollectionManager collectionManager;

    private ActiveListAdapter(Context context, int resource, ArrayList<Swrl> swrls, CollectionManager collectionManager) {
        super(context, resource, swrls);
        this.swrls = swrls;
        this.collectionManager = collectionManager;
    }
    ActiveListAdapter(Context context, int resource, CollectionManager collectionManager) {
        this(context, resource, (ArrayList<Swrl>) collectionManager.getActive(), collectionManager);
    }


    @Override
    public void insert(Swrl swrl, int index) {
        super.insert(swrl, index);
        collectionManager.save(swrl);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = createRowLayoutIfNull(convertView);

        Swrl swrl = swrls.get(position);
        if (swrl != null){
            setTitle(row, swrl, position);
            setDoneButton(row, swrl);
        }

        return row;
    }

    private View createRowLayoutIfNull(View row) {
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, null);
        }
        return row;
    }

    private void setTitle(View row, final Swrl swrl, final int index) {
        TextView title = (TextView) row.findViewById(R.id.list_title);
        title.setText(swrl.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewActivity = new Intent(getContext(), ViewActivity.class);
                viewActivity.putExtra(EXTRAS_SWRLS, swrls);
                viewActivity.putExtra(EXTRAS_INDEX, index);
                startActivity((Activity) getContext(), viewActivity, null);
            }
        });
    }

    private void setDoneButton(View row, final Swrl swrl) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swrls.remove(swrl);
                collectionManager.markAsDone(swrl);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 250);
            }
        });

    }
}
