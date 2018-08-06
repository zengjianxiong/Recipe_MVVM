package com.zfl.recipe_mvvm.fragment;

import android.support.v7.widget.LinearLayoutManager;

import com.recipe_mvvm.zfl.lib_base.mvvm.BaseFragment;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.CommonRecyclerViewAdapter;
import com.zfl.recipe_mvvm.R;
import com.recipe_mvvm.zfl.lib_res.widget.bga_refresh.RecipeStyleRefreshViewHolder;
import com.zfl.recipe_mvvm.databinding.FragmentRecipeListBinding;
import com.zfl.recipe_mvvm.vm.RecipeListViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends BaseFragment<RecipeListViewModel, FragmentRecipeListBinding>
{

    private CommonRecyclerViewAdapter mAdapter;

    private List<RecipeInfo> mRecipeList;

    private String mRecipeId;//菜单分类ID，等viewModel初始化完成后才传进去

    private String mRecipeLabel;//主标签，显示在最前排，等viewModel初始化完成后才传进去

    @Override
    protected void lazyLoad()
    {
        mViewModel.lazyLoad();
    }

    @Override
    protected RecipeListViewModel getViewModel()
    {
        RecipeListViewModel viewModel = new RecipeListViewModel(mViewDataBinding);
        viewModel.setRecipeId(mRecipeId);
        viewModel.setRecipeLabel(mRecipeLabel);
        setVisibleHintListener(viewModel);
        return viewModel;
    }

    @Override
    protected int getLayoutResID()
    {
        return R.layout.fragment_recipe_list;
    }

    @Override
    protected void initData()
    {
        mRecipeList = new ArrayList<>();
        mAdapter = new CommonRecyclerViewAdapter(getActivity(), mRecipeList, mViewModel, mViewModel);
        mViewDataBinding.rvRecipeList.setAdapter(mAdapter);
        mViewModel.setAdapter(mAdapter);
        mViewModel.setRecipeList(mRecipeList);
    }

    @Override
    protected void initView()
    {
        mViewDataBinding.bgaRlRecipe.setRefreshViewHolder(new RecipeStyleRefreshViewHolder(getActivity(), true));
        mViewDataBinding.rvRecipeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mViewDataBinding.bgaRlRecipe.setDelegate(mViewModel);
    }

    /**
     * 设置菜单id以便进行请求
     * @param id
     */
    public void setRecipeId(String id) {
        mRecipeId = id;
//        mViewModel.setRecipeId(id);
        //这个id也用于识别Fragment，来让Fragment进行刷新
    }

    /**
     * 设置主标签以便进行显示
     * @param label
     */
    public void setRecipeLabel(String label) {
        mRecipeLabel = label;
//        mViewModel.setRecipeLabel(label);
    }
}
