package co.swrl.list.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;

import static android.support.v4.app.ActivityCompat.startActivity;


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

    public void refreshList(){
        clear();
        addAll(collectionManager.getActive());
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
            setBackground(row, swrl);
            setTitle(row, swrl, position);
            setDoneButton(row, swrl, position);
        }

        return row;
    }

    private void setBackground(View row, Swrl swrl) {
        row.setBackgroundResource(swrl.getType().getRowBorderResource());
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
                viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, swrls);
                viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, index);
                startActivity((Activity) getContext(), viewActivity, null);
            }
        });
    }

    private void setDoneButton(final View row, final Swrl swrl, final int position) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation done = getDoneAnimation(swrl, row, position);
                row.startAnimation(done);
            }
        });

    }

    @NonNull
    private Animation getDoneAnimation(final Swrl swrl, final View row, final int position) {
        final Animation delete = AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_out);
        delete.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                showUndoSnackbar(swrl, row, position);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                markSwrlAsDone(swrl);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return delete;
    }

    private void markSwrlAsDone(Swrl swrl) {
        swrls.remove(swrl);
        collectionManager.markAsDone(swrl);
        notifyDataSetChanged();
    }

    private void showUndoSnackbar(final Swrl swrl, View row, final int position) {
        String undoTitle = "\"" + swrl.getTitle() + "\" " + "marked as done";
        Snackbar.make(row, undoTitle, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                reAddSwrl(position, swrl);
            }
        }).show();
    }

    private void reAddSwrl(int position, Swrl swrl) {
        swrls.add(position, swrl);
        collectionManager.markAsActive(swrl);
        notifyDataSetChanged();
    }
}
