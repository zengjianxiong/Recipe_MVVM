package com.recipe_mvvm.zfl.module_recipe_about;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseActivity;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.utils.ARouterPathUtil;
import com.recipe_mvvm.zfl.module_recipe_about.databinding.ActivityRecipeAboutBinding;

@Route(path = ARouterPathUtil.RecipeAboutActivity)
public class RecipeAboutActivity extends BaseActivity<BaseViewModel, ActivityRecipeAboutBinding>
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BaseViewModel getViewModel()
    {
        return null;
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe_about;
    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initView()
    {
        setSupportActionBar(mViewDataBinding.tbRecipeAbout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewDataBinding.tbRecipeAbout.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
        mViewDataBinding.ctlRecipeAbout.setTitle("关于Recipe");
        mViewDataBinding.ctlRecipeAbout.setContentScrimColor(mContext.getResources().getColor(R.color.colorPrimary));
        mViewDataBinding.ctlRecipeAbout.setStatusBarScrimColor(mContext.getResources().getColor(R.color
                .colorPrimary));

        //展开时字体设置为透明
        mViewDataBinding.ctlRecipeAbout.setExpandedTitleColor(mContext.getResources().getColor(android.R.color.transparent));
        mViewDataBinding.ctlRecipeAbout.setCollapsedTitleTextColor(Color.WHITE);
    }

}
