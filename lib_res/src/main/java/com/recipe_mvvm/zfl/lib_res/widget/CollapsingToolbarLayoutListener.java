package com.recipe_mvvm.zfl.lib_res.widget;

import android.support.design.widget.AppBarLayout;

/**
 * Created by ZFL on 2016/11/8.<br>
 *     这个专门用于监听CollapsingToolbarLayout的折叠或者展开状态
 */
public abstract class CollapsingToolbarLayoutListener implements AppBarLayout.OnOffsetChangedListener{

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED, verticalOffset);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED, verticalOffset);
            }
            mCurrentState = State.COLLAPSED;
        } else {
//            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE, verticalOffset);
//            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset);
}
