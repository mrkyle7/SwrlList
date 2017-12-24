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

import java.util.concurrent.atomic.AtomicInteger;

import co.swrl.list.R;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.list.swrllists.SwrlListRecyclerAdapter;

public class SwipeSimpleCallback extends ItemTouchHelper.SimpleCallback {

    private static final String LOG_TAG = "SwipeSimpleCallback";
    private ListActivity listActivity;
    private final RecyclerView recyclerView;
    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private Drawable swipeLeftBackground;
    private Drawable swipeRightBackground;
    private Drawable leftIconMark;
    private Drawable rightIconMark;
    private int xMarkMargin;
    private boolean initiated;
    private final SwipeItemDecoration swipeItemDecoration;
    private final AtomicInteger swipeLeftColor;
    private final AtomicInteger swipeLeftIcon;
    private final AtomicInteger swipeRightColor;
    private final AtomicInteger swipeRightIcon;
    private int lastSwipeDir;

    public SwipeSimpleCallback(ListActivity listActivity, RecyclerView recyclerView,
                               SwipeItemDecoration swipeItemDecoration,
                               AtomicInteger swipeLeftColor, AtomicInteger swipeLeftIcon,
                               AtomicInteger swipeRightColor, AtomicInteger SwipeRightIcon) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listActivity = listActivity;
        this.recyclerView = recyclerView;
        this.swipeItemDecoration = swipeItemDecoration;
        this.swipeLeftColor = swipeLeftColor;
        this.swipeLeftIcon = swipeLeftIcon;
        this.swipeRightColor = swipeRightColor;
        swipeRightIcon = SwipeRightIcon;
    }

    private void init() {
        Log.d(LOG_TAG, "Initiating Item Touch Helper");
        Log.d(LOG_TAG, "Colors: left: " + swipeLeftColor + " right: " + swipeRightColor);
        Log.d(LOG_TAG, "Icons: left: " + swipeLeftIcon + " right: " + swipeRightIcon);
        //noinspection deprecation
        swipeLeftBackground = new ColorDrawable(listActivity.getResources().getColor(swipeLeftColor.intValue()));
        swipeRightBackground = new ColorDrawable(listActivity.getResources().getColor(swipeRightColor.intValue()));
        leftIconMark = ContextCompat.getDrawable(listActivity, swipeLeftIcon.intValue());
        leftIconMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        rightIconMark = ContextCompat.getDrawable(listActivity, swipeRightIcon.intValue());
        rightIconMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
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
        if (lastSwipeDir != swipeDir){
            lastSwipeDir = swipeDir;
            int newAnimationColor = swipeDir == ItemTouchHelper.RIGHT ? listActivity.swipeRightColor.intValue() : listActivity.swipeLeftColor.intValue();
            listActivity.animationColor.set(newAnimationColor);
            swipeItemDecoration.forceReDraw();
        }
        int swipedPosition = viewHolder.getAdapterPosition();
        SwrlListRecyclerAdapter adapter = (SwrlListRecyclerAdapter) recyclerView.getAdapter();
        if (swipeDir == ItemTouchHelper.RIGHT) {
            adapter.swipeRightAction(viewHolder, swipedPosition);
        } else {
            adapter.swipeLeftAction(viewHolder, swipedPosition);
        }
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

        if (swipeRight(dX)) {
            swipeRightBackground.setBounds(itemView.getLeft() - (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            swipeRightBackground.draw(c);
        } else {
            swipeLeftBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            swipeLeftBackground.draw(c);
        }

        // draw mark
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth;
        int intrinsicHeight;
        if (swipeRight(dX)) {
            intrinsicWidth = rightIconMark.getIntrinsicWidth();
            intrinsicHeight = rightIconMark.getIntrinsicWidth();
        } else {
            intrinsicWidth = leftIconMark.getIntrinsicWidth();
            intrinsicHeight = leftIconMark.getIntrinsicWidth();
        }

        int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int xMarkBottom = xMarkTop + intrinsicHeight;

        int xMarkLeft;
        int xMarkRight;

        if (swipeRight(dX)) {
            xMarkLeft = itemView.getLeft() + xMarkMargin;
            xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
            rightIconMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            rightIconMark.draw(c);
        } else {
            xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            xMarkRight = itemView.getRight() - xMarkMargin;
            leftIconMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            leftIconMark.draw(c);
        }


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private boolean swipeRight(float dX) {
        return dX > 0;
    }

}
