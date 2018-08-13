package com.zfl.recipe_mvvm.vm;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.MenuItem;

import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe.RecipeCategoryBean;
import com.recipe_mvvm.zfl.lib_base.utils.ApiUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ConstantUtil;
import com.recipe_mvvm.zfl.lib_base.utils.RandomUtil;
import com.zfl.recipe_mvvm.R;
import com.zfl.recipe_mvvm.databinding.ActivityRecipeBinding;
import com.zfl.recipe_mvvm.fragment.RecipeListFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 处理菜单首页，单例模式
 * @Author ZFL
 * @Date 2018/7/27
 */
public class RecipeViewModel extends BaseViewModel<ActivityRecipeBinding>
{

    //16个菜单图标合集
    private int[] MenuIconRes = new int[]{
            R.mipmap.nav_recipe_category_01,
            R.mipmap.nav_recipe_category_02,
            R.mipmap.nav_recipe_category_03,
            R.mipmap.nav_recipe_category_04,
            R.mipmap.nav_recipe_category_05,
            R.mipmap.nav_recipe_category_06,
            R.mipmap.nav_recipe_category_07,
            R.mipmap.nav_recipe_category_08,
            R.mipmap.nav_recipe_category_09,
            R.mipmap.nav_recipe_category_10,
            R.mipmap.nav_recipe_category_11,
            R.mipmap.nav_recipe_category_12,
            R.mipmap.nav_recipe_category_13,
            R.mipmap.nav_recipe_category_14,
            R.mipmap.nav_recipe_category_15,
            R.mipmap.nav_recipe_category_16};

    //为了生成随机而不重复的菜单图标
    private SparseArray<Integer> mMenuIconResPositions = new SparseArray<>();

    //加载的进度对话框
    private ProgressDialog mLoadingDialog;

    private RecipeCategoryBean mCategoryBean;


    //记录之前的选中项来清除Checked状态
    private MenuItem mPreMenuItem;

    //用于适配TabLayout的Tab和相关内容的List
    private List<RecipeCategoryBean.ChildsBean> mRecipeTabList;

    private List<Fragment> mRecipeListFrgs;
    //用于装填fragment，在activity中初始化
    private FragmentManager mFragmentManager;


    public RecipeViewModel(ActivityRecipeBinding viewDataBinding, FragmentManager manager)
    {
        super(viewDataBinding);
        mFragmentManager = manager;
    }

    public void setRecipeTabList(List<RecipeCategoryBean.ChildsBean> list) {
        mRecipeTabList = list;
    }

    public void setRecipeListFrgs(List<Fragment> list) {
        mRecipeListFrgs = list;
    }

    @Override
    protected void start()
    {
        get(ApiUtil.URL_RECIPE_CATEGORY, RecipeCategoryBean.class, true, true);
    }

    @Override
    protected void clear()
    {

    }

    public void updateCategory(RecipeCategoryBean bean) {
        //lambda
        viewDataBinding.nvRecipeCategory.setNavigationItemSelectedListener((item -> {
            if (null != mPreMenuItem)
            {
                mPreMenuItem.setChecked(false);
            }
            item.setChecked(true);
            mPreMenuItem = item;
            viewDataBinding.dlRecipe.closeDrawers();
            updateRecipeListFrg(item.getItemId());
            return false;
        }));
        mMenuIconResPositions.clear();
        mCategoryBean = bean;
        List<Integer> menuIconRes = RandomUtil.random(MenuIconRes, mCategoryBean.result.childs
                .size());
        for (int i = 0; i < mCategoryBean.result.childs.size(); i++)
        {
            viewDataBinding.nvRecipeCategory.getMenu().add(0, i, 0, mCategoryBean.result.childs.get(i)
                    .categoryInfo.name).setIcon(menuIconRes.get(i));
        }
        //TODO 在这里开始更新fragment
        //默认第一个选中
        mPreMenuItem = viewDataBinding.nvRecipeCategory.getMenu().getItem(0).setChecked(true);
        updateRecipeListFrg(0);
    }

    //更新顶部图片以及tab和fragment
    private void updateRecipeListFrg(int index)
    {
        RecipeCategoryBean.ChildsBean bean = mCategoryBean.result.childs.get(index);
        mRecipeTabList.clear();
        mRecipeListFrgs.clear();
        mRecipeTabList.addAll(bean.childs);
        for (RecipeCategoryBean.ChildsBean bean1 : mRecipeTabList)
        {
            RecipeListFragment frg = new RecipeListFragment();
            frg.setRecipeId(bean1.categoryInfo.ctgId);
            frg.setRecipeLabel(bean1.categoryInfo.name);//设置主标签
            mRecipeListFrgs.add(frg);
        }
        viewDataBinding.includeRecipeContent.vpRecipeList.setAdapter(new FragmentPagerAdapter(mFragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                return mRecipeListFrgs.get(position);
            }

            @Override
            public int getCount()
            {
                return mRecipeListFrgs.size();
            }

            @Override
            public CharSequence getPageTitle(int position)
            {
                return mRecipeTabList.get(position).categoryInfo.name;
            }

            @Override
            public long getItemId(int position)
            {
                return Long.parseLong(mRecipeTabList.get(position).categoryInfo.ctgId);
            }
        });
        //至少4页
        viewDataBinding.includeRecipeContent.vpRecipeList.setOffscreenPageLimit(4);
        viewDataBinding.includeRecipeContent.tlRecipeCategory.getTabAt(0).select();
    }



    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeCategoryBean.class, "updateCategory");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> categoryParamMap = new HashMap<>();
        categoryParamMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        map.put(RecipeCategoryBean.class, categoryParamMap);
        return map;
    }

    @Override
    public void showLoading()
    {
        mLoadingDialog = ProgressDialog.show(viewDataBinding.getRoot().getContext(), "请求菜谱类别", "请求中......", true, false);
    }

    @Override
    public void hideLoading()
    {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
        {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

}
