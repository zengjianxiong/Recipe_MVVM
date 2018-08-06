package com.recipe_mvvm.zfl.module_recipe_detail.vm;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.recipe_mvvm.zfl.lib_base.event_bus_msg_entity.UpdateListEvent;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeDetail;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeFavorBean;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe_detail.RecipeDetailBean;
import com.recipe_mvvm.zfl.lib_base.utils.ApiUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ConstantUtil;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.OnClickHandler;
import com.recipe_mvvm.zfl.module_recipe_detail.R;
import com.recipe_mvvm.zfl.module_recipe_detail.databinding.ActivityRecipeDetailBinding;
import com.recipe_mvvm.zfl.module_recipe_detail.fragment.RecipeDetailMainFragment;
import com.recipe_mvvm.zfl.module_recipe_detail.fragment.RecipeDetailMethodFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailViewModel extends BaseViewModel<ActivityRecipeDetailBinding> implements OnClickHandler
{

    private String mMenuId;

    private RecipeDetailBean mDetailBean;

    private ProgressDialog mLoadingDialog;

    private List<Fragment> mRecipeMethodFrgs;

    //用于装填fragment，在activity中初始化
    private FragmentManager mFragmentManager;

    private boolean mIsFavor = false;//判断该菜谱是否为收藏菜谱

    public RecipeDetailViewModel(ActivityRecipeDetailBinding viewDataBinding, FragmentManager manager)
    {
        super(viewDataBinding);
        mFragmentManager = manager;
    }

    public void setRecipeMethodFrgs(List<Fragment> fragments) {
        mRecipeMethodFrgs = fragments;
    }

    @Override
    protected void start()
    {
        //获取菜谱详情数据
        get(ApiUtil.URL_RECIPE_DETAIL, RecipeDetailBean.class, true, true);
    }

    @Override
    protected void clear()
    {
        hideLoading();
    }

    public void setMenuId(String id) {
        mMenuId = id;
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeDetailBean.class, "updateRecipeDetail");
        map.put(RecipeFavorBean.class, "recipeEventDeliver");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        detailMap.put(ConstantUtil.ID, mMenuId);
        map.put(RecipeDetailBean.class, detailMap);
        Map<String, Object> recipeEventMap = new HashMap<>();
        recipeEventMap.put(ConstantUtil.MENU_ID, mMenuId);
        if (mDetailBean != null) {
            recipeEventMap.put(ConstantUtil.CTG_TITLES, mDetailBean.result.ctgTitles);
            recipeEventMap.put(ConstantUtil.THUMBNAIL, mDetailBean.result.thumbnail);
            recipeEventMap.put(ConstantUtil.NAME, mDetailBean.result.name);
        }
        map.put(RecipeFavorBean.class, recipeEventMap);
        return map;
    }

    public void updateRecipeDetail(RecipeDetailBean bean)
    {
        mDetailBean = bean;
        //获取图片
        viewDataBinding.setBgUrl(mDetailBean.result.recipe.img);
        //先添加主要MainFragment
        RecipeDetailMainFragment mainFragment = new RecipeDetailMainFragment();
        mainFragment.setDetailBean(mDetailBean);
        mRecipeMethodFrgs.add(mainFragment);

        if (!TextUtils.isEmpty(mDetailBean.result.recipe.method))
        {
            //对methodBean进行转换
            mDetailBean.result.recipe.convertMethodBean();
            //如果有方法步骤，再根据方法步骤，添加方法fragment
            for (RecipeDetail.MethodBean.MethodDetailBean bean1 : mDetailBean.result.recipe
                    .methodBean.list)
            {
                RecipeDetailMethodFragment methodFragment = new RecipeDetailMethodFragment();
                methodFragment.setDetailBean(bean1);
                mRecipeMethodFrgs.add(methodFragment);
            }
        }

        //在fragment添加到ViewPager上
        viewDataBinding.vpRecipeMethod.setAdapter(new FragmentPagerAdapter(mFragmentManager)
        {
            @Override
            public Fragment getItem(int position)
            {
                return mRecipeMethodFrgs.get(position);
            }

            @Override
            public int getCount()
            {
                return mRecipeMethodFrgs.size();
            }
        });
        viewDataBinding.vpRecipeMethod.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        //全部加载
        viewDataBinding.vpRecipeMethod.setOffscreenPageLimit(mRecipeMethodFrgs.size());

        //判断该Recipe是否为收藏Recipe
        get(ApiUtil.URL_IS_FAVOR_RECIPE, RecipeFavorBean.class, false, false);
    }

    public void recipeEventDeliver(RecipeFavorBean bean) {
        switch (bean.recipeEventType) {
            case 1://添加收藏成功
                addRecipeSuccess(bean);
                break;
            case 2://删除收藏成功
                deleteRecipeSuccess(bean);
                break;
            case 3://判断是否是收藏菜谱
                isFavorRecipe(bean);
                break;
        }
    }

    private void addRecipeSuccess(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        Toast.makeText(viewDataBinding.getRoot().getContext(), "菜谱: " + mDetailBean.result.name + " 收藏成功", Toast.LENGTH_SHORT)
                .show();
        //收藏成功
        mIsFavor = true;
        showFavorIcon(mIsFavor);
        //发送收藏菜谱列表更新的通知
//        Apollo.emit(ConstantUtil.FAVOR_RECIPE_LIST_UPDATE);
        EventBus.getDefault().post(new UpdateListEvent());
    }


    private void deleteRecipeSuccess(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        Toast.makeText(viewDataBinding.getRoot().getContext(), "菜谱: " + mDetailBean.result.name + " 移出收藏列表成功", Toast
                .LENGTH_SHORT).show();
        //删除收藏成功
        mIsFavor = false;
        showFavorIcon(mIsFavor);
        //发送收藏菜谱列表更新的通知
//        Apollo.emit(ConstantUtil.FAVOR_RECIPE_LIST_UPDATE);
        EventBus.getDefault().post(new UpdateListEvent());
    }

    public void isFavorRecipe(RecipeFavorBean bean)
    {
        if (!mDetailBean.result.menuId.equals(bean.menuId)) return;
        mIsFavor = bean.isFavor;
        if (!mIsFavor)
        {
            //不是收藏菜谱，收藏图标设置为白色
            viewDataBinding.ivRecipeCollect.setImageResource(R.mipmap.menu_uncollect);
        } else
        {
            //是收藏菜谱，收藏图标设置为红色
            viewDataBinding.ivRecipeCollect.setImageResource(R.mipmap.menu_collected);

        }
    }

    private void showFavorIcon(boolean isFavor)
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        viewDataBinding.ivRecipeCollect.clearAnimation();
        viewDataBinding.ivRecipeCollect.startAnimation(scaleAnimation);
        viewDataBinding.ivRecipeCollect.setVisibility(View.VISIBLE);
        if (isFavor)
        {
            viewDataBinding.ivRecipeCollect.setImageResource(R.mipmap.menu_collected);
        } else
        {
            viewDataBinding.ivRecipeCollect.setImageResource(R.mipmap.menu_uncollect);
        }
    }

    private void hideFavorIcon()
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                //至少让动画完整播放
                if (mIsFavor)
                {
                    post(ApiUtil.URL_DELETE_FAVOR_RECIPE, RecipeFavorBean.class, false, true);
                } else
                {
                    post(ApiUtil.URL_ADD_FAVOR_RECIPE, RecipeFavorBean.class, false, true);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        viewDataBinding.ivRecipeCollect.clearAnimation();
        viewDataBinding.ivRecipeCollect.startAnimation(scaleAnimation);
        viewDataBinding.ivRecipeCollect.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLoading()
    {
        mLoadingDialog = ProgressDialog.show(viewDataBinding.getRoot().getContext(), "请求菜谱详细内容", "请求中......", true, false);
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

    @Override
    public void onRootClick(View view)
    {

    }

    @Override
    public void onRootLongClick(View view)
    {

    }

    @Override
    public void onChildClick(View view)
    {
        //library中不允许switch case的语法去比较view的id
        if (view.getId() == R.id.ivRecipeCollect) {
            //在这里处理收藏按钮的点击事件
            //点击后进行菜谱收藏或者菜谱删除
            if (mDetailBean == null)
                return;
            //同时进行收藏按钮动画
            hideFavorIcon();
        }
    }

    @Override
    public void onChildLongClick(View view)
    {

    }
}
