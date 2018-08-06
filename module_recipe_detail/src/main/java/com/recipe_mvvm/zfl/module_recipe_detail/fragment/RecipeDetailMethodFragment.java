package com.recipe_mvvm.zfl.module_recipe_detail.fragment;

import com.recipe_mvvm.zfl.lib_base.mvvm.BaseFragment;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeDetail;
import com.recipe_mvvm.zfl.module_recipe_detail.R;
import com.recipe_mvvm.zfl.module_recipe_detail.databinding.FragmentRecipeMethodBinding;

/**
 * @Description 菜谱制作步骤
 * @Author ZFL
 * @Date 2018/8/1
 */
public class RecipeDetailMethodFragment extends BaseFragment<BaseViewModel, FragmentRecipeMethodBinding>
{

    private RecipeDetail.MethodBean.MethodDetailBean mDetailBean;//详细步骤

    public void setDetailBean(RecipeDetail.MethodBean.MethodDetailBean bean) {
        mDetailBean = bean;
    }

    @Override
    protected void lazyLoad()
    {
        mViewDataBinding.setMethodDetail(mDetailBean);
    }

    @Override
    protected BaseViewModel getViewModel()
    {
        return null;
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_method;
    }

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initView()
    {

    }
}
