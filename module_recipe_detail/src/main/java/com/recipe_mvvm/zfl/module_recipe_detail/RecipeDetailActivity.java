package com.recipe_mvvm.zfl.module_recipe_detail;

import android.graphics.Outline;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseActivity;
import com.recipe_mvvm.zfl.lib_base.utils.ARouterPathUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ConstantUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ScreenUtil;
import com.recipe_mvvm.zfl.module_recipe_detail.databinding.ActivityRecipeDetailBinding;
import com.recipe_mvvm.zfl.module_recipe_detail.vm.RecipeDetailViewModel;

import java.util.ArrayList;
import java.util.List;

@Route(path = ARouterPathUtil.RecipeDetailActivity)
public class RecipeDetailActivity extends BaseActivity<RecipeDetailViewModel, ActivityRecipeDetailBinding>
{

    private String mToolBarTitle = "";

    private List<Fragment> mRecipeMethodFrgs;

    @Override
    protected RecipeDetailViewModel getViewModel()
    {
        RecipeDetailViewModel viewModel = new RecipeDetailViewModel(mViewDataBinding, getSupportFragmentManager());
        mViewDataBinding.setClickHandler(viewModel);
        return viewModel;
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.activity_recipe_detail;
    }

    @Override
    protected void initData()
    {
        mViewModel.setMenuId(getIntent().getStringExtra(ConstantUtil.RECIPE_MENU_ID));
        mRecipeMethodFrgs = new ArrayList<>();
        mViewModel.setRecipeMethodFrgs(mRecipeMethodFrgs);
    }

    @Override
    protected void initView()
    {
        mViewDataBinding.tbRecipeDetail.setTitle(mToolBarTitle);

        setSupportActionBar(mViewDataBinding.tbRecipeDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewDataBinding.tbRecipeDetail.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                outline.setOval(0, 0, ScreenUtil.dp2px(mContext, 6f), ScreenUtil.dp2px(mContext,
                        6f));
            }
        };
        mViewDataBinding.ivRecipeCollect.setOutlineProvider(viewOutlineProvider);
    }

    @Override
    protected boolean isNeedEventBus()
    {
        return false;
    }
}
