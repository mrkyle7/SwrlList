package co.swrl.list.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
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
import co.swrl.list.item.Type;
import co.swrl.list.ui.ViewActivity.ViewType;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.view.View.GONE;
import static co.swrl.list.R.drawable.ic_add_black_24dp;
import static co.swrl.list.R.drawable.ic_done_black_24dp;
import static co.swrl.list.ui.ViewActivity.ViewType.ADD;
import static co.swrl.list.ui.ViewActivity.ViewType.VIEW;


class SwrlListAdapter extends ArrayAdapter<Swrl> {
    private ArrayList<?> originalSwrls;
    private int position;
    private final CollectionManager collectionManager;
    private final ListType listType;
    private final Swrl originalSwrl;

    private enum ListType {
        SEARCH_RESULTS,
        VIEW_SEARCH_RESULTS;
    }

    public static SwrlListAdapter getResultsListAdapter(Context context, CollectionManager collectionManager) {
        return new SwrlListAdapter(
                context,
                R.id.list_row,
                new ArrayList<Swrl>(),
                collectionManager,
                ListType.SEARCH_RESULTS,
                null,
                null,
                0);
    }

    public static SwrlListAdapter getViewResultsListAdapter(Context context, CollectionManager collectionManager, Swrl originalSwrl, ArrayList<?> originalSwrls, int position) {
        return new SwrlListAdapter(
                context,
                R.id.list_row,
                new ArrayList<Swrl>(),
                collectionManager,
                ListType.VIEW_SEARCH_RESULTS,
                originalSwrl,
                originalSwrls,
                position);
    }

    private SwrlListAdapter(Context context, int resource, ArrayList<Swrl> swrls, CollectionManager collectionManager, ListType listType, Swrl originalSwrl, ArrayList<?> originalSwrls, int position) {
        super(context, resource, swrls);
        this.collectionManager = collectionManager;
        this.listType = listType;
        this.originalSwrl = originalSwrl;
        this.originalSwrls = originalSwrls;
        this.position = position;
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
            setSubtitle2(row, swrl);
            setImage(row, swrl);
            switch (listType) {
                case SEARCH_RESULTS:
                    setAddButton(row, swrl);
                    setClickableRow(row, position, ADD);
                    break;
                case VIEW_SEARCH_RESULTS:
                    setReplaceOnClick(row, swrl, originalSwrl);
                    break;
            }
        }

        return row;
    }

    private void setTitle(View row, final Swrl swrl) {
        TextView title = (TextView) row.findViewById(R.id.list_title);
        title.setText(swrl.getTitle());
        title.setSelected(true);
    }

    private void setSubTitle(View row, Swrl swrl) {
        TextView subtitle = (TextView) row.findViewById(R.id.list_subtitle1);
        String subtitleText = swrl.getType().getFriendlyName();
        if (swrl.getDetails() != null) {
            switch (swrl.getType()) {
                case FILM:
                    if (swrl.getDetails().getActors() != null) subtitleText = swrl.getDetails().getActors();
                    break;
                case TV:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case BOOK:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case ALBUM:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case VIDEO_GAME:
                    if (swrl.getDetails().getPlatform() != null) subtitleText = swrl.getDetails().getPlatform();
                    break;
                case BOARD_GAME:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case APP:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case PODCAST:
                    if (swrl.getDetails().getCreator() != null) subtitleText = swrl.getDetails().getCreator();
                    break;
                case UNKNOWN:
                    break;
            }
        }
        subtitle.setText(subtitleText);
    }

    private void setSubtitle2(View row, Swrl swrl) {
        TextView subtitle2 = (TextView) row.findViewById(R.id.list_subtitle2);
        String subtitle2Text = "No Details...";
        if (swrl.getDetails() != null && swrl.getDetails().getCategories() != null
                && !swrl.getDetails().getCategories().isEmpty()) {
            subtitle2Text = TextUtils.join(", ", swrl.getDetails().getCategories());
        }
        if (swrl.getType() == Type.BOOK && swrl.getDetails() != null && swrl.getDetails().getPublicationDate() != null){
            subtitle2Text = swrl.getDetails().getPublicationDate();
        }
        subtitle2.setText(subtitle2Text);
    }

    private void setClickableRow(View row, final int position, final ViewType viewType) {
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewActivity = new Intent(getContext(), ViewActivity.class);
                viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, getAllItems());
                viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, position);
                viewActivity.putExtra(ViewActivity.EXTRAS_TYPE, viewType);
                startActivity(getContext(), viewActivity, null);
            }
        });
    }

    private void setImage(View row, Swrl swrl) {
        final ImageView thumbnail = (ImageView) row.findViewById(R.id.list_image);
        thumbnail.setBackgroundColor(Color.TRANSPARENT);
        int iconResource = swrl.getType().getIcon();
        View imageBackground = row.findViewById(R.id.row_left_border);
        int color = getContext().getResources().getColor(swrl.getType().getColor());
        imageBackground.setBackgroundColor(color);
        Details details = swrl.getDetails();
        if (details != null && details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
            resizeThumbnailForIcon(thumbnail);
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
        thumbnail.getLayoutParams().height = getDPI(80);
        thumbnail.getLayoutParams().width = getDPI(65);
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

    private void setReplaceOnClick(final View row, final Swrl swrl, final Swrl originalSwrl) {
        ImageButton button = (ImageButton) row.findViewById(R.id.list_item_button);
        button.setVisibility(GONE);
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionManager.saveDetails(originalSwrl, swrl.getDetails());
                collectionManager.updateTitle(originalSwrl, swrl.getTitle());
                ArrayList<Swrl> newSwrls = new ArrayList<>();
                for (Object originalSwrl : originalSwrls) {
                    newSwrls.add((Swrl) originalSwrl);
                }
                newSwrls.remove(originalSwrl);
                newSwrls.add(position, swrl);
                Activity activity = (Activity) getContext();
                activity.finish();
                Intent viewActivity = new Intent(activity, ViewActivity.class);
                viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, newSwrls);
                viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, position);
                viewActivity.putExtra(ViewActivity.EXTRAS_TYPE, VIEW);
                startActivity(getContext(), viewActivity, null);
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
