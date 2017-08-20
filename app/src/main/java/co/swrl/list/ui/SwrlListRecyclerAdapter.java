package co.swrl.list.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static android.support.v4.content.ContextCompat.startActivity;
import static co.swrl.list.ui.ViewActivity.ViewType.VIEW;


public class SwrlListRecyclerAdapter extends RecyclerView.Adapter {

    private final Context context;
    private List<Swrl> swrls;
    private final CollectionManager collectionManager;

    public SwrlListRecyclerAdapter(Context context, CollectionManager collectionManager) {
        this.context = context;
        this.collectionManager = collectionManager;
        swrls = collectionManager.getActive();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SwrlRow(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SwrlRow swrlRow = (SwrlRow) holder;
        Swrl swrl = swrls.get(position);

        setTitle(swrlRow, swrl);
        setSubTitle(swrlRow, swrl);
        setSubtitle2(swrlRow, swrl);
        setImage(swrlRow, swrl);
        setClickableRow(swrlRow, position, VIEW);
    }

    private void setTitle(SwrlRow swrlRow, final Swrl swrl) {
        swrlRow.title.setText(swrl.getTitle());
        swrlRow.title.setSelected(true);
    }

    private void setSubTitle(SwrlRow swrlRow, Swrl swrl) {
        String subtitleText = swrl.getType().getFriendlyName();
        if (swrl.getDetails() != null) {
            switch (swrl.getType()) {
                case FILM:
                    if (swrl.getDetails().getActors() != null)
                        subtitleText = swrl.getDetails().getActors();
                    break;
                case TV:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case BOOK:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case ALBUM:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case VIDEO_GAME:
                    if (swrl.getDetails().getPlatform() != null)
                        subtitleText = swrl.getDetails().getPlatform();
                    break;
                case BOARD_GAME:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case APP:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case PODCAST:
                    if (swrl.getDetails().getCreator() != null)
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case UNKNOWN:
                    break;
            }
        }
        swrlRow.subtitle.setText(subtitleText);
    }

    private void setSubtitle2(SwrlRow swrlRow, Swrl swrl) {
        String subtitle2Text = "No Details...";
        if (swrl.getDetails() != null && swrl.getDetails().getCategories() != null
                && !swrl.getDetails().getCategories().isEmpty()) {
            subtitle2Text = TextUtils.join(", ", swrl.getDetails().getCategories());
        }
        if (swrl.getType() == Type.BOOK && swrl.getDetails() != null && swrl.getDetails().getPublicationDate() != null) {
            subtitle2Text = swrl.getDetails().getPublicationDate();
        }
        swrlRow.subtitle2.setText(subtitle2Text);
    }

    private void setImage(SwrlRow swrlRow, Swrl swrl) {
        final ImageView thumbnail = swrlRow.thumbnail;
        thumbnail.setBackgroundColor(Color.TRANSPARENT);
        int iconResource = swrl.getType().getIcon();
        View imageBackground = swrlRow.imageBackground;
        int color = context.getResources().getColor(swrl.getType().getColor());
        imageBackground.setBackgroundColor(color);
        Details details = swrl.getDetails();

        if (details != null && details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "")) {
            resizeThumbnailForImage(thumbnail);
            Picasso.with(context)
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
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

    private void setClickableRow(SwrlRow swrlRow, final int position, final ViewActivity.ViewType viewType) {
        swrlRow.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewActivity = new Intent(context, ViewActivity.class);
                viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, (Serializable) swrls);
                viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, position);
                viewActivity.putExtra(ViewActivity.EXTRAS_TYPE, viewType);
                viewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(context, viewActivity, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return swrls.size();
    }

    public void refreshAll() {
        swrls.clear();
        swrls.addAll(collectionManager.getActive());
        notifyDataSetChanged();
    }

    public void markAsDone(RecyclerView.ViewHolder viewHolder, int position) {
        Swrl swrlToRemove = swrls.get(position);
        if (swrls.contains(swrlToRemove)) {
            swrls.remove(position);
            collectionManager.markAsDone(swrlToRemove);
            notifyItemRemoved(position);
            showUndoSnackbar(swrlToRemove, viewHolder.itemView, position);
        }
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
        notifyItemInserted(position);
    }

    private static class SwrlRow extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView subtitle;
        private final TextView subtitle2;
        private final ImageView thumbnail;
        private final View imageBackground;

        SwrlRow(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false));
            title = (TextView) itemView.findViewById(R.id.list_title);
            subtitle = (TextView) itemView.findViewById(R.id.list_subtitle1);
            subtitle2 = (TextView) itemView.findViewById(R.id.list_subtitle2);
            thumbnail = (ImageView) itemView.findViewById(R.id.list_image);
            imageBackground = itemView.findViewById(R.id.row_left_border);
        }

        View getView() {
            return itemView;
        }
    }
}
