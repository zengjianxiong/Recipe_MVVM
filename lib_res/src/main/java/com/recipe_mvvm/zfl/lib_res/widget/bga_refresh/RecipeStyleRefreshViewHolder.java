package com.recipe_mvvm.zfl.lib_res.widget.bga_refresh;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.recipe_mvvm.zfl.lib_res.R;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;


/**
 * @Description 自制的食谱风格的下拉刷新，上拉加载View
 * @Author ZFL
 * @Date 2017/6/29.
 */

public class RecipeStyleRefreshViewHolder extends BGARefreshViewHolder implements Animator.AnimatorListener
{

    private ImageView mIvHeaderIcon;

    private TextView mTvHeaderStatus;

    private ObjectAnimator mAnimHeaderRefreshing; //采用属性动画，解决动画停止时，图片强行复位的问题

    private String mPullDownRefreshText = "下拉刷新";
    private String mReleaseRefreshText = "释放刷新";
    private String mRefreshingText = "刷新中..";
    private String mEndRefreshingText = "刷新完成";

    private ImageView mIvFooterIcon;

    private TextView mTvFooterStatus;

    private ObjectAnimator mAnimFooterLoadMore;

    /**
     * @param context
     * @param isLoadingMoreEnabled 上拉加载更多是否可用
     */
    public RecipeStyleRefreshViewHolder(Context context, boolean isLoadingMoreEnabled)
    {
        super(context, isLoadingMoreEnabled);
    }

    @Override
    public void setRefreshLayout(BGARefreshLayout refreshLayout) {
        mRefreshLayout = refreshLayout;
        //添加HeaderView回弹监听
        mRefreshLayout.setHeaderViewBackListener(this);
    }


    private void initRefreshAnim() {
        //初始化属性旋转动画
        mAnimHeaderRefreshing = ObjectAnimator.ofFloat(mIvHeaderIcon, "rotation", 0f, 360f);
        LinearInterpolator lin = new LinearInterpolator();
        mAnimHeaderRefreshing.setInterpolator(lin);
        mAnimHeaderRefreshing.setDuration(700);
        mAnimHeaderRefreshing.setRepeatCount(-1);
        mAnimHeaderRefreshing.setRepeatMode(ValueAnimator.RESTART);
    }

    private void initLoadMoreAnim() {
        mAnimFooterLoadMore = ObjectAnimator.ofFloat(mIvFooterIcon, "rotation", 0f, 360f);
        LinearInterpolator lin = new LinearInterpolator();
        mAnimFooterLoadMore.setInterpolator(lin);
        mAnimFooterLoadMore.setDuration(700);
        mAnimFooterLoadMore.setRepeatCount(-1);
        mAnimFooterLoadMore.setRepeatMode(ValueAnimator.RESTART);
    }


    @Override
    public View getRefreshHeaderView()
    {
        if (mRefreshHeaderView == null) {

            mRefreshHeaderView = View.inflate(mContext, R.layout.header_recipe_refresh, null);
            mIvHeaderIcon = mRefreshHeaderView.findViewById(R.id.ivRefreshHeaderIcon);
            mTvHeaderStatus = mRefreshHeaderView.findViewById(R.id.tvRefreshHeaderStatus);
            initRefreshAnim();
        }
        return mRefreshHeaderView;
    }

    @Override
    public View getLoadMoreFooterView()
    {
        if (mLoadMoreFooterView == null) {
            mLoadMoreFooterView = View.inflate(mContext, R.layout.footer_recipe_loadmore, null);
            mIvFooterIcon = mLoadMoreFooterView.findViewById(R.id.ivRefreshFooterIcon);
            mTvFooterStatus = mLoadMoreFooterView.findViewById(R.id.tvRefreshFooterStatus);
            initLoadMoreAnim();
        }
        return mLoadMoreFooterView;
    }

    @Override
    public void handleScale(float scale, int moveYDistance)
    {
//        LogUtil.e("scale:" + scale + " moveYDistance:" + moveYDistance);
    }

    @Override
    public void changeToIdle()
    {

    }

    @Override
    public void changeToPullDown()
    {
        mTvHeaderStatus.setText(mPullDownRefreshText);
    }

    @Override
    public void changeToReleaseRefresh()
    {
        mTvHeaderStatus.setText(mReleaseRefreshText);
    }

    @Override
    public void changeToRefreshing()
    {
        mTvHeaderStatus.setText(mRefreshingText);
        if (mAnimHeaderRefreshing.isStarted()) {
            mAnimHeaderRefreshing.resume();
        } else {
            mAnimHeaderRefreshing.start();
        }
    }

    @Override
    public void onEndRefreshing()
    {
        mTvHeaderStatus.setText(mEndRefreshingText);
        mAnimHeaderRefreshing.pause();
    }

    @Override
    public void changeToLoadingMore()
    {
        if (mAnimFooterLoadMore.isStarted()) {
            mAnimFooterLoadMore.resume();
        } else {
            mAnimFooterLoadMore.start();
        }
    }

    @Override
    public void onEndLoadingMore()
    {
        mAnimFooterLoadMore.pause();
    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
        //主要用于监听headerView回弹结束
        mTvHeaderStatus.setText(mPullDownRefreshText);
    }

    @Override
    public void onAnimationStart(Animator animation)
    {

    }

    @Override
    public void onAnimationCancel(Animator animation)
    {

    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {

    }
}
