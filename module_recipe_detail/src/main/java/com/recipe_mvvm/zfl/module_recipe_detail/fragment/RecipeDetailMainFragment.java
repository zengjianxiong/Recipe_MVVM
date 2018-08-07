package com.recipe_mvvm.zfl.module_recipe_detail.fragment;

import android.text.TextUtils;

import com.recipe_mvvm.zfl.lib_base.mvvm.BaseFragment;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe_detail.RecipeDetailBean;
import com.recipe_mvvm.zfl.lib_res.widget.utils.LabelUtil;
import com.recipe_mvvm.zfl.module_recipe_detail.R;
import com.recipe_mvvm.zfl.module_recipe_detail.databinding.FragmentRecipeDetailMainBinding;
/**
 * @Description 这个Fragment主要介绍菜谱的类别，简介
 * @Author ZFL
 * @Date 2018/8/1
 */
public class RecipeDetailMainFragment extends BaseFragment<BaseViewModel, FragmentRecipeDetailMainBinding>
{

    private RecipeDetailBean mDetailBean;

    public void setDetailBean(RecipeDetailBean bean)
    {
        mDetailBean = bean;

    }

    @Override
    protected void lazyLoad()
    {
        //开始使用数据对界面进行初始化
        mViewDataBinding.tvRecipeDetailTitle.setText(mDetailBean.result.recipe.title);
        mViewDataBinding.tvRecipeDetailSummary.setText("简介：" + mDetailBean.result.recipe.sumary);
        //去除多余字符
        if (!TextUtils.isEmpty(mDetailBean.result.recipe.ingredients)) {
            mViewDataBinding.tvRecipeDetailIngredients.setText("食材：" + mDetailBean.result.recipe.ingredients.replace("[\"", "").replace("\"]", ""));
        }
        //插入标签
        String[] labels = mDetailBean.result.ctgTitles.split(",");
        for (int i = 0; i < labels.length; i++)
        {
            LabelUtil.addLabelView(getActivity(), mViewDataBinding.llRecipeDetailLabelArea, labels[i], 17);
        }
    }

    @Override
    protected BaseViewModel getViewModel()
    {
        return null;
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_detail_main;
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
