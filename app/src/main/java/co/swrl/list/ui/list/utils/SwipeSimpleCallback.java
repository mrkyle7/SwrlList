package co.swrl.list.ui.list.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import co.swrl.list.R;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.list.swrllists.SwrlListRecyclerAdapter;

public class SwipeSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private static final String LOG_TAG = "SwipeSimpleCallback";
    private ListActivity listActivity;
    private final RecyclerView recyclerView;
    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private Drawable background;
    private Drawable xMark;
    private int xMarkMargin;
    private boolean initiated;
    private final int swipeColor;
    private final int swipeIcon;

    public SwipeSimpleCallback(ListActivity listActivity, RecyclerView recyclerView, int swipeColor, int swipeIcon) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listActivity = listActivity;
        this.recyclerView = recyclerView;
        this.swipeColor = swipeColor;
        this.swipeIcon = swipeIcon;
    }

    private void init() {
        Log.d(LOG_TAG, "Initiating Item Touch Helper");
        //noinspection deprecation
        background = new ColorDrawable(listActivity.getResources().getColor(swipeColor));
        xMark = ContextCompat.getDrawable(listActivity, swipeIcon);
        xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        xMarkMargin = (int) listActivity.getResources().getDimension(R.dimen.ic_clear_margin);
        initiated = true;
    }

    public void forceReDraw() {
        initiated = false;
    }

    // not important, we don't want drag & drop
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
        int swipedPosition = viewHolder.getAdapterPosition();
        SwrlListRecyclerAdapter adapter = (SwrlListRecyclerAdapter) recyclerView.getAdapter();
        adapter.swipeLeftAction(viewHolder, swipedPosition);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        listActivity.enableDisableSwipeRefresh(actionState == ItemTouchHelper.ACTION_STATE_IDLE);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        if (!initiated) {
            init();
        }

        // draw green background
        if (dX > 0) { //swipe right
            background.setBounds(itemView.getLeft() - (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else {
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        }
        background.draw(c);

        // draw mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = xMark.getIntrinsicWidth();
        int intrinsicHeight = xMark.getIntrinsicWidth();

        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;

        int xMarkLeft;
        int xMarkRight;

        if (dX > 0) { //swipe Right
            xMarkLeft = itemView.getLeft() + xMarkMargin;
            xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
        } else {
            xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            xMarkRight = itemView.getRight() - xMarkMargin;
        }
        xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
        xMark.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
