package co.swrl.list.ui.list.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.swrl.list.R;
import co.swrl.list.utils.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.actions.SwrlCoActions;
import co.swrl.list.item.search.Search;
import co.swrl.list.ui.activity.ViewActivity;
import co.swrl.list.users.SwrlUserHelpers;

import static android.support.v4.content.ContextCompat.startActivity;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static co.swrl.list.R.drawable.ic_add_black_24dp;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.VIEW;


public class SwrlRow extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView subtitle;
    private final TextView subtitle2;
    private final ImageView thumbnail;
    private final View imageBackground;
    private final ImageButton addButton;
    private final ImageView profileImage;

    public SwrlRow(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false));
        title = (TextView) itemView.findViewById(R.id.list_title);
        subtitle = (TextView) itemView.findViewById(R.id.list_subtitle1);
        subtitle2 = (TextView) itemView.findViewById(R.id.list_subtitle2);
        thumbnail = (ImageView) itemView.findViewById(R.id.list_image);
        imageBackground = itemView.findViewById(R.id.row_left_border);
        addButton = (ImageButton) itemView.findViewById(R.id.list_item_button);
        profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
    }

    public void setProfileImage(Swrl swrl, final Context context) {
        if (authorURLExists(swrl)) {
            profileImage.setVisibility(VISIBLE);
            Picasso picasso = Picasso.with(context);
//            picasso.setIndicatorsEnabled(true);
            picasso.setLoggingEnabled(true);
            picasso.load(swrl.getAuthorAvatarURL())
                    .placeholder(R.drawable.progress_spinner)
                    .error(R.drawable.ic_person_black_24dp)
                    .resize(150, 150)
                    .centerInside()
                    .noFade()
                    .into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            profileImage.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {
                            profileImage.setImageResource(R.drawable.ic_person_black_24dp);
                        }
                    });
        } else {
            profileImage.setVisibility(GONE);
        }
    }

    private boolean authorURLExists(Swrl swrl) {
        return swrl.getAuthorAvatarURL() != null && !swrl.getAuthorAvatarURL().isEmpty();
    }

    public void setTitle(final Swrl swrl) {
        title.setText(swrl.getTitle());
        title.setSelected(true);
    }

    public void setSubTitle(Swrl swrl) {
        String subtitleText = swrl.getType().getFriendlyName();
        if (swrl.getDetails() != null) {
            switch (swrl.getType()) {
                case FILM:
                    if (swrl.getDetails().getActors() != null && !swrl.getDetails().getActors().isEmpty())
                        subtitleText = swrl.getDetails().getActors();
                    break;
                case TV:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case BOOK:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case ALBUM:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case VIDEO_GAME:
                    if (swrl.getDetails().getPlatform() != null && !swrl.getDetails().getPlatform().isEmpty())
                        subtitleText = swrl.getDetails().getPlatform();
                    break;
                case BOARD_GAME:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case APP:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case PODCAST:
                    if (swrl.getDetails().getCreator() != null && !swrl.getDetails().getCreator().isEmpty())
                        subtitleText = swrl.getDetails().getCreator();
                    break;
                case UNKNOWN:
                    break;
            }
        }
        subtitle.setText(subtitleText);
    }

    public void setSubtitle2(Swrl swrl) {
        String subtitle2Text = "";
        if (swrl.getDetails() == null) {
            subtitle2Text = "No details, click to search";
        } else if (swrl.getDetails().getCategories() != null
                && !swrl.getDetails().getCategories().isEmpty()) {
            subtitle2Text = swrl.getDetails().getCategories();
        } else if (swrl.getDetails().getPublicationDate() != null
                && !swrl.getDetails().getPublicationDate().isEmpty()) {
            subtitle2Text = swrl.getDetails().getPublicationDate();
        } else if (swrl.getDetails().getOverview() != null
                && !swrl.getDetails().getOverview().isEmpty()) {
            subtitle2Text = swrl.getDetails().getOverview();
        }
        subtitle2.setText(subtitle2Text);
    }

    public void setImage(Swrl swrl, final Context context) {
        thumbnail.setBackgroundColor(Color.TRANSPARENT);
        int iconResource = swrl.getType().getIcon();
        int color = context.getResources().getColor(swrl.getType().getColor());
        imageBackground.setBackgroundColor(color);
        Details details = swrl.getDetails();

        if (posterURLexists(details)) {
            resizeThumbnailForIcon(thumbnail, context);
            Picasso picasso = Picasso.with(context);
//            picasso.setIndicatorsEnabled(true);
//            picasso.setLoggingEnabled(true);
            picasso.load(details.getPosterURL())
                    .placeholder(R.drawable.progress_spinner)
                    .error(iconResource)
                    .resize(600, 600)
                    .centerInside()
                    .noFade()
                    .into(thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            resizeThumbnailForImage(thumbnail, context);
                        }

                        @Override
                        public void onError() {
                            resizeThumbnailForIcon(thumbnail, context);
                        }
                    });

        } else {
            thumbnail.setImageResource(iconResource);
            resizeThumbnailForIcon(thumbnail, context);
        }
    }

    private boolean posterURLexists(Details details) {
        return details != null && details.getPosterURL() != null && !Objects.equals(details.getPosterURL(), "");
    }

    private void resizeThumbnailForIcon(ImageView thumbnail, Context context) {
        thumbnail.getLayoutParams().height = getDPI(40, context);
        thumbnail.getLayoutParams().width = getDPI(40, context);
    }

    private void resizeThumbnailForImage(ImageView thumbnail, Context context) {
        thumbnail.getLayoutParams().height = getDPI(80, context);
        thumbnail.getLayoutParams().width = getDPI(65, context);
    }

    private int getDPI(int i, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

    public void setRowClickToOpenViewByType(final int position, final List<Swrl> swrls, final Context context, final ViewActivity.ViewType viewType) {
        itemView.setOnClickListener(new View.OnClickListener() {
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

    public void setAddButton(final Swrl swrl, final CollectionManager collectionManager, final Activity activity) {
        addButton.setImageResource(ic_add_black_24dp);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionManager.save(swrl);
                final AsyncTask<Swrl, Void, Void> getSwrlDetails = getSwrlDetailsAsyncTask();
                getSwrlDetails.execute(swrl);
            }

            @NonNull
            private AsyncTask<Swrl, Void, Void> getSwrlDetailsAsyncTask() {
                return new AsyncTask<Swrl, Void, Void>() {
                    private ProgressDialog addingDialog;

                    @Override
                    protected void onPreExecute() {
                        addingDialog = new ProgressDialog(activity);
                        addingDialog.setMessage("Adding Swrl...");
                        addingDialog.show();
                    }

                    @Override
                    protected void onPostExecute(Void voids) {
                        addingDialog.hide();
                        activity.finish();
                    }

                    @Override
                    protected void onCancelled(Void voids) {
                        addingDialog.hide();
                        activity.finish();
                    }

                    @Override
                    protected Void doInBackground(Swrl... params) {
                        Swrl swrl = params[0];

                        updateUserAvatarURL(swrl, activity, collectionManager);

                        Details details = null;

                        try {
                            Search search = swrl.getType().getSearch();
                            details = search.byID(swrl.getDetails().getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (details != null) {
                            swrl.setDetails(details);
                            collectionManager.saveDetails(swrl, details);
                        }

                        SwrlPreferences preferences = new SwrlPreferences(activity);

                        if(preferences.loggedIn()) {
                            SwrlCoActions.create(swrl, SwrlCoActions.LATER, preferences, collectionManager);
                        }
                        return null;
                    }
                };
            }

        });
    }

    public void setRowClickToReplaceViewWithDetails(final Swrl swrl, final Swrl originalSwrl, final List<Swrl> originalSwrls, final int position, final CollectionManager collectionManager, final Activity activity) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AsyncTask<Swrl, Void, Details> getSwrlDetails = getSwrlDetailsAsyncTask();
                getSwrlDetails.execute(swrl);
            }

            @NonNull
            private AsyncTask<Swrl, Void, Details> getSwrlDetailsAsyncTask() {
                return new AsyncTask<Swrl, Void, Details>() {
                    private ProgressDialog addingDialog;

                    @Override
                    protected void onPreExecute() {
                        addingDialog = new ProgressDialog(activity);
                        addingDialog.setMessage("Adding Swrl...");
                        addingDialog.show();
                    }

                    @Override
                    protected void onPostExecute(Details details) {
                        updateSwrlWithDetailsAndRefreshView(details);
                    }


                    @Override
                    protected void onCancelled(Details details) {
                        updateSwrlWithDetailsAndRefreshView(details);
                    }

                    private void updateSwrlWithDetailsAndRefreshView(Details details) {
                        addingDialog.hide();
                        if (details != null) {
                            collectionManager.saveDetails(originalSwrl, details);
                            collectionManager.updateTitle(originalSwrl, details.getTitle());
                            swrl.setDetails(details);
                        } else {
                            collectionManager.saveDetails(originalSwrl, swrl.getDetails());
                            collectionManager.updateTitle(originalSwrl, swrl.getTitle());
                        }
                        ArrayList<Swrl> newSwrls = new ArrayList<>();
                        for (Swrl originalSwrl : originalSwrls) {
                            newSwrls.add(originalSwrl);
                        }
                        newSwrls.remove(originalSwrl);
                        newSwrls.add(position, swrl);
                        activity.finish();
                        Intent viewActivity = new Intent(activity, ViewActivity.class);
                        viewActivity.putExtra(ViewActivity.EXTRAS_SWRLS, newSwrls);
                        viewActivity.putExtra(ViewActivity.EXTRAS_INDEX, position);
                        viewActivity.putExtra(ViewActivity.EXTRAS_TYPE, VIEW);
                        startActivity(activity, viewActivity, null);
                    }

                    @Override
                    protected Details doInBackground(Swrl... params) {
                        Swrl swrl = params[0];

                        updateUserAvatarURL(swrl, activity, collectionManager);

                        Details details = null;

                        try {
                            Search search = swrl.getType().getSearch();
                            details = search.byID(swrl.getDetails().getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return details;
                    }
                };
            }
        });
    }

    private void updateUserAvatarURL(Swrl swrl, Activity activity, CollectionManager collectionManager) {
        SwrlPreferences preferences = new SwrlPreferences(activity);

        int userID = preferences.getUserID();
        if (userID != 0 && userID != -1) {
            swrl.setAuthorId(userID);
            collectionManager.save(swrl);
            String avatarURL = SwrlUserHelpers.getUserAvatarURL(userID);
            if (avatarURL != null) {
                collectionManager.updateAuthorAvatarURL(swrl, avatarURL);
            }
        }
    }
}

