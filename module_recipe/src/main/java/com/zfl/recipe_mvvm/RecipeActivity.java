package com.zfl.recipe_mvvm;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseActivity;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe.RecipeCategoryBean;
import com.recipe_mvvm.zfl.lib_base.utils.ARouterPathUtil;
import com.recipe_mvvm.zfl.lib_res.widget.CollapsingToolbarLayoutListener;
import com.zfl.recipe_mvvm.databinding.ActivityRecipeBinding;
import com.zfl.recipe_mvvm.vm.RecipeViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
@Route(path = ARouterPathUtil.RecipeActivity)
public class RecipeActivity extends BaseActivity<RecipeViewModel, ActivityRecipeBinding>
{

    //用于设置动画效果
    private TextView mTvToolBarTitle;

    private String mToolBarTitle = "选择你喜欢的类别哦";

    //TabLayout的背景View是否出现
    private boolean mIsAppBarLayoutBgShow = true;

    private AnimationDrawable mLoadingDrawable;


    //用于适配TabLayout的Tab和相关内容的List
    private List<RecipeCategoryBean.ChildsBean> mRecipeTabList;

    private List<Fragment> mRecipeListFrgs;

    //为了防止重复加载背景图片
    private String mBgUrl;

    @Override
    protected RecipeViewModel getViewModel()
    {

        return new RecipeViewModel(mViewDataBinding, getSupportFragmentManager());
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe;
    }

    @Override
    protected void initData()
    {
        mRecipeListFrgs = new ArrayList<>();
        mRecipeTabList = new ArrayList<>();
        //将值传进去
        mViewModel.setRecipeTabList(mRecipeTabList);
        mViewModel.setRecipeListFrgs(mRecipeListFrgs);
    }

    @Override
    protected void initView()
    {
        mViewDataBinding.includeRecipeContent.tlRecipeCategory.setupWithViewPager(mViewDataBinding.includeRecipeContent.vpRecipeList);
        mViewDataBinding.includeRecipeContent.tbRecipeMain.setTitle(mToolBarTitle);
        setSupportActionBar(mViewDataBinding.includeRecipeContent.tbRecipeMain);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mViewDataBinding.dlRecipe, mViewDataBinding.includeRecipeContent.tbRecipeMain,
                0, 0)
        {
            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
            }
        };
        toggle.syncState();
        mViewDataBinding.dlRecipe.addDrawerListener(toggle);
        //获取ToolBar的TitleTextView，以便设置动画效果
        for (int i = 0; i < mViewDataBinding.includeRecipeContent.tbRecipeMain.getChildCount(); i++)
        {
            View v = mViewDataBinding.includeRecipeContent.tbRecipeMain.getChildAt(i);
            if (v != null && v instanceof TextView)
            {
                TextView tv = (TextView) v;
                if (mToolBarTitle.equals(tv.getText().toString()))
                {
                    mTvToolBarTitle = tv;
                }
            }
        }

        //一开始默认titleTextView是隐藏的
        mTvToolBarTitle.setVisibility(View.INVISIBLE);

        mViewDataBinding.includeRecipeContent.ctlRecipeMain.setContentScrimColor(mContext.getResources().getColor(R.color.colorPrimary));
        mViewDataBinding.includeRecipeContent.ctlRecipeMain.setStatusBarScrimColor(mContext.getResources().getColor(R.color
                .colorPrimary));
        mViewDataBinding.includeRecipeContent.ctlRecipeMain.setTitleEnabled(false);
        //        mCtlRecipeMain.setTitle();
        //        //展开时字体设置为透明
        //        mCtlRecipeMain.setExpandedTitleColor(mContext.getResources().getColor(android.R
        // .color.transparent));
        //        mCtlRecipeMain.setCollapsedTitleTextColor(Color.WHITE);
        //控制TabLayout的背景出现
        mViewDataBinding.includeRecipeContent.ablRecipeMain.addOnOffsetChangedListener(new CollapsingToolbarLayoutListener()
        {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset)
            {
                double totalScrollRange = appBarLayout.getTotalScrollRange();
                double scale = totalScrollRange / (Math.abs((double) verticalOffset));
                if (scale <= 3.2)
                {
                    showOrHideAppBarLayoutBg(false);
                }
                if (scale >= 3.3)
                {
                    showOrHideAppBarLayoutBg(true);
                }
            }
        });

        mViewDataBinding.nvRecipeCategory.setItemIconTintList(null);


        mLoadingDrawable = (AnimationDrawable) mViewDataBinding.includeRecipeContent.ivRecipeLoading.getDrawable();

    }

    @Override
    protected boolean isNeedEventBus()
    {
        return true;
    }

    private void showOrHideAppBarLayoutBg(final boolean show)
    {
        if (mIsAppBarLayoutBgShow == show)
            return;//如果状态相同，不执行动画
        AlphaAnimation tabAnimation;
        AlphaAnimation titleAnimation;
        if (show)
        {
            tabAnimation = new AlphaAnimation(0, 1);
            titleAnimation = new AlphaAnimation(1, 0);
        } else
        {
            tabAnimation = new AlphaAnimation(1, 0);
            titleAnimation = new AlphaAnimation(0, 1);
        }
        tabAnimation.setDuration(700);
        titleAnimation.setDuration(700);
        tabAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                if (show)
                {
                    mViewDataBinding.includeRecipeContent.vTlBackground.setVisibility(View.VISIBLE);
                    mTvToolBarTitle.setVisibility(View.INVISIBLE);
                } else
                {
                    mViewDataBinding.includeRecipeContent.vTlBackground.setVisibility(View.INVISIBLE);
                    mTvToolBarTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        mViewDataBinding.includeRecipeContent.vTlBackground.clearAnimation();
        mTvToolBarTitle.clearAnimation();
        mViewDataBinding.includeRecipeContent.vTlBackground.startAnimation(tabAnimation);
        mTvToolBarTitle.startAnimation(titleAnimation);
        mIsAppBarLayoutBgShow = show;
    }

    /**
     * 使用event_bus接收信息，进行图片背景更新
     * 使用mvvm的模式(bindingAdapter)进行更新，非常麻烦，所以直接进行更新
     * @param bgUrl
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveUpdateMainBg(final String bgUrl) {
        if (bgUrl.equals(mBgUrl)) return;
        mViewDataBinding.includeRecipeContent.ivRecipeLoading.setVisibility(View.VISIBLE);
        mLoadingDrawable.start();
        mViewDataBinding.includeRecipeContent.ivRecipeMainBg.setImageDrawable(null);
        mViewDataBinding.includeRecipeContent.ivRecipeMainBg.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!TextUtils.isEmpty(bgUrl))
                {
                    RequestOptions requestOptions = new RequestOptions().fallback(R.mipmap.main_bg_default5);
                    Glide.with(mContext)
                            .load(bgUrl)
                            .apply(requestOptions)
                            .into(new ImageViewTarget<Drawable>(mViewDataBinding.includeRecipeContent.ivRecipeMainBg)
                            {
                                @Override
                                protected void setResource(@Nullable Drawable resource)
                                {
                                    mViewDataBinding.includeRecipeContent.ivRecipeMainBg.setImageDrawable(resource);
                                }

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                                {
                                    //加载成功监听
                                    mLoadingDrawable.stop();
                                    mViewDataBinding.includeRecipeContent.ivRecipeLoading.setVisibility(View.INVISIBLE);
                                    mBgUrl = bgUrl;
                                    super.onResourceReady(resource, transition);
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable)
                                {
                                    //加载成功监听
                                    mLoadingDrawable.stop();
                                    mViewDataBinding.includeRecipeContent.ivRecipeLoading.setVisibility(View.INVISIBLE);
                                    super.onLoadFailed(errorDrawable);
                                }

                            });
                }
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_recipe_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //前往其他activity均使用ARouter导航
            case R.id.menuMyCollect:
                //前往收藏菜谱界面
                ARouter.getInstance().build(ARouterPathUtil.RecipeFavorActivity).navigation();
                break;
            case R.id.menuAbout:
                //前往“关于Recipe”的Activity
                ARouter.getInstance().build(ARouterPathUtil.RecipeAboutActivity).navigation();
                break;
        }
        return true;
    }
}
