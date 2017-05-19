package co.swrl.list.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;

import static android.support.v4.app.ActivityCompat.startActivity;
import static co.swrl.list.R.drawable.ic_add_black_24dp;
import static co.swrl.list.R.drawable.ic_done_black_24dp;


class SwrlListAdapter extends ArrayAdapter<Swrl> {
    private final CollectionManager collectionManager;
    private final ListType listType;

    private enum ListType {
        ACTIVE_SWRLS,
        SEARCH_RESULTS;
    }

    public static SwrlListAdapter getActiveListAdapter(Context context, CollectionManager collectionManager) {
        return new SwrlListAdapter(
                context,
                R.id.list_row,
                (ArrayList<Swrl>) collectionManager.getActive(),
                collectionManager,
                ListType.ACTIVE_SWRLS);
    }

    public static SwrlListAdapter getResultsListAdapter(Context context, CollectionManager collectionManager) {
        return new SwrlListAdapter(
                context,
                R.id.list_row,
                new ArrayList<Swrl>(),
                collectionManager,
                ListType.SEARCH_RESULTS);
    }

    private SwrlListAdapter(Context context, int resource, ArrayList<Swrl> swrls, CollectionManager collectionManager, ListType listType) {
        super(context, resource, swrls);
        this.collectionManager = collectionManager;
        this.listType = listType;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = createRowLayoutIfNull(convertView);
        Swrl swrl = getItem(position);
        if (swrl != null) {
            setTitle(row, swrl);
            setSubTitle(row, swrl);
            setImage(row, swrl);
            switch (listType) {
                case ACTIVE_SWRLS:
                    setDoneButton(row, swrl, position);
                    setClickableRow(row, position);
                    break;
                case SEARCH_RESULTS:
                    setAddButton(row, swrl);
                    break;
            }
        }

        return row;
    }

    private void setTitle(View row, final Swrl swrl) {
        TextView title = (TextView) row.findViewById(R.id.list_title);
        title.setText(swrl.getTitle());
    }

    private void setSubTitle(View row, Swrl swrl) {
        TextView subtitle = (TextView) row.findViewById(R.id.list_subtitle);
        subtitle.setText(swrl.getType().getFriendlyName());
    }

    private void setClickableRow(View row, final int position) {
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewActivity = new Intent(getContext(), ViewActivity.class);
                viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, getAllItems());
                viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, position);
                startActivity((Activity) getContext(), viewActivity, null);
            }
        });
    }

    private void setImage(View row, Swrl swrl) {
        final ImageView thumbnail = (ImageView) row.findViewById(R.id.list_image);
        int iconResource = swrl.getType().getIcon();
        View imageBackground = row.findViewById(R.id.row_left_border);
        int color = getContext().getResources().getColor(swrl.getType().getColor());
        imageBackground.setBackgroundColor(color);
        Details details = swrl.getDetails();

        if (details != null && details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
            Picasso.with(getContext())
                    .load(details.getPosterURL())
                    .placeholder(R.drawable.progress_spinner)
                    .error(iconResource)
                    .into(thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            resizeThumbnailForImage(thumbnail);
                        }

                        @Override
                        public void onError() {
                            resizeThumbnailForIcon(thumbnail);
                        }
                    });

        } else {
            thumbnail.setImageResource(iconResource);
            resizeThumbnailForIcon(thumbnail);
        }
    }

    private void resizeThumbnailForIcon(ImageView thumbnail) {
        thumbnail.getLayoutParams().height = getDPI(40);
        thumbnail.getLayoutParams().width = getDPI(40);
    }

    private void resizeThumbnailForImage(ImageView thumbnail) {
        thumbnail.getLayoutParams().height = getDPI(50);
        thumbnail.getLayoutParams().width = getDPI(50);
    }


    private int getDPI(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getContext().getResources().getDisplayMetrics());
    }

    private View createRowLayoutIfNull(View row) {
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, null);
        }
        return row;
    }

    private ArrayList<Swrl> getAllItems() {
        ArrayList<Swrl> swrls = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            Swrl swrl = getItem(i);
            swrls.add(swrl);
        }
        return swrls;
    }

    private void setAddButton(final View row, final Swrl swrl) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_button);
        button.setImageResource(ic_add_black_24dp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionManager.save(swrl);
                Activity addActivity = (Activity) getContext();
                addActivity.finish();
            }
        });
    }

    private void setDoneButton(final View row, final Swrl swrl, final int position) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_button);
        button.setImageResource(ic_done_black_24dp);
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
        remove(swrl);
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
        insert(swrl, position);
        collectionManager.markAsActive(swrl);
        notifyDataSetChanged();
    }
}
