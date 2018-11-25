package com.progscrape.ui;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ViewAnimator;

import java.lang.reflect.Field;

// https://code.google.com/p/android/issues/detail?id=78191
public class FixedSwipeRefreshLayout extends SwipeRefreshLayout {
    private Field targetField;

    public FixedSwipeRefreshLayout(Context context) {
        super(context);
    }

    public FixedSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        try {
            targetField = getClass().getSuperclass().getDeclaredField("mTarget");
            targetField.setAccessible(true);
        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
        }
    }

    @Override
    public boolean canChildScrollUp() {
        View target;

        try {
            target = (View) targetField.get(this);
        } catch (Exception e) {
            // Should not happen
            e.printStackTrace();
            return super.canChildScrollUp();
        }

        if (target instanceof ViewAnimator) {
            target = ((ViewAnimator)target).getCurrentView();
        }

        if (target instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) target;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                int position = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                return position != 0;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] positions = ((StaggeredGridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPositions(null);
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == 0) {
                        return false;
                    }
                }
            }
            return true;
        }

        return super.canChildScrollUp();
    }
}
