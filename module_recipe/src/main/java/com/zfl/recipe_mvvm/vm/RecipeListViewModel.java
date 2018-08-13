package com.zfl.recipe_mvvm.vm;


import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.Toast;

import com.lsxiao.apollo.core.Apollo;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseFragment;
import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe_list.RecipeListBean;
import com.recipe_mvvm.zfl.lib_base.utils.ApiUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ConstantUtil;
import com.recipe_mvvm.zfl.lib_res.databinding.ItemRecipeListBinding;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.BaseViewHolder;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.CommonRecyclerViewAdapter;
import com.recipe_mvvm.zfl.lib_res.widget.holder.RecipeListItemHolder;
import com.zfl.recipe_mvvm.R;
import com.zfl.recipe_mvvm.databinding.FragmentRecipeListBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import io.reactivex.Observable;

/**
 * @Description 处理recipeListFragment的数据与界面的逻辑
 * @Author ZFL
 * @Date 2018/7/30
 */
public class RecipeListViewModel extends BaseViewModel<FragmentRecipeListBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate,
        CommonRecyclerViewAdapter.AdapterTemplate, CommonRecyclerViewAdapter.AdapterEmitter, BaseFragment.OnUserVisibleHintListener
{


    private String mRecipeId;//菜单分类ID

    private int mCurPage = 1;//当前页数

    private int mTotalPage = 1;//总页数，要进行计算

    private int mSize = 20;//每页的数据条数，默认20

    private CommonRecyclerViewAdapter mAdapter;

    private boolean mIsRefresh = false;//判断用户是下拉刷新还是上拉加载更多

    private List<RecipeInfo> mRecipeList;

    private boolean mIsLazyLoad = false;//判断是否是懒加载

    private boolean mIsUpdateMainBgSend = false;//避免重复发送多次更新主页背景图的请求

    private String mRecipeLabel;//主标签，显示在最前排

    private boolean mIsVisibleToUser = false;//判断该fragment是否对用户可见


    public RecipeListViewModel(FragmentRecipeListBinding viewDataBinding)
    {
        super(viewDataBinding);

    }

    /**
     * 设置菜单id以便进行请求
     * @param id
     */
    public void setRecipeId(String id) {
        mRecipeId = id;
        //这个id也用于识别Fragment，来让Fragment进行刷新
    }

    /**
     * 设置主标签以便进行显示
     * @param label
     */
    public void setRecipeLabel(String label) {
        mRecipeLabel = label;
    }

    public void setRecipeList(List<RecipeInfo> list) {
        mRecipeList = list;
    }

    public void setAdapter(CommonRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public void lazyLoad() {
        mIsLazyLoad = true;
        viewDataBinding.bgaRlRecipe.beginRefreshing();
    }



    @Override
    protected void start()
    {
        //do nothing
    }

    @Override
    protected void clear()
    {

    }

    public void updateRecipeList(RecipeListBean bean) {
        //计算总页数
        if (bean.result.total % mSize == 0) {
            mTotalPage = bean.result.total / mSize;
        } else {
            mTotalPage = bean.result.total / mSize + 1;
        }
        if (mIsRefresh) {
            mRecipeList.clear();
            viewDataBinding.bgaRlRecipe.endRefreshing();
        } else {
            viewDataBinding.bgaRlRecipe.endLoadingMore();
        }
        for (RecipeInfo info : bean.result.list) {
            //设置主标签
            info.mainLabel = mRecipeLabel;
            mRecipeList.add(info);
        }
        mAdapter.notifyDataSetChanged();
        //在请求成功后，不管是不是懒加载，都可以将mIsLazyLoad布尔值设置为false，因为懒加载只执行一次
        mIsLazyLoad = false;
        //请求成功后，如果此Fragment处于可见状态，发送主页更新背景图片的请求
        //发送请求因素：
        //1.Fragment处于可见状态
        //2.数据是下拉刷新获得的
        //3.之前还没有发送过更新
        String mainBgUrl = getMainBgUrl();
        if (mIsVisibleToUser && mIsRefresh && (!mIsUpdateMainBgSend)) {
//            EventBus.getDefault().post(mainBgUrl);
            Apollo.emit(ConstantUtil.UPDATE_MAIN_BACKGROUND, mainBgUrl);
            mIsUpdateMainBgSend = true;
        }
    }

    @Override
    public void onUserVisibleHint(boolean isVisibleToUser)
    {
        mIsVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            //表示滑动到这个Fragment了，如果已经拥有数据，可以发送更新背景图的请求
            if (mRecipeList != null && mRecipeList.size() != 0 && !mIsUpdateMainBgSend) {
                String mainBgUrl = getMainBgUrl();
//                EventBus.getDefault().post(mainBgUrl);
                Apollo.emit(ConstantUtil.UPDATE_MAIN_BACKGROUND, mainBgUrl);
                mIsUpdateMainBgSend = true;
            }
        } else {
            //滑动到其他Fragment后，首页图片也会进行更换。
            //将此布尔值置为false，以便用户再滑动回来后再次发送更新主页背景图的请求
            mIsUpdateMainBgSend = false;
        }
    }

    /**
     * 在刷新成功后，获取主页背景图的Url
     */
    private String getMainBgUrl() {
        String mainBgUrl = null;
        for (RecipeInfo info : mRecipeList) {
            if (!TextUtils.isEmpty(info.recipe.img)) {
                mainBgUrl = info.recipe.img.trim();
                break;
            }
        }

        return mainBgUrl;
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeListBean.class, "updateRecipeList");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> paramsMap = new HashMap<>();
        Map<String, Object> recipeListMap = new HashMap<>();
        recipeListMap.put(ConstantUtil.KEY, ApiUtil.APPKEY);
        recipeListMap.put("cid", mRecipeId);
        recipeListMap.put("page", mCurPage);
        recipeListMap.put("size", mSize);
        paramsMap.put(RecipeListBean.class, recipeListMap);
        return paramsMap;
    }

    @Override
    public void showLoading()
    {
        //do nothing
    }

    @Override
    public void hideLoading()
    {
        //do nothing
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        mIsRefresh = true;
        mCurPage = 1;
        if (mIsLazyLoad) {
            //延迟1.5秒（至少让用户能看见完整的动画啊）
            //lambda
            viewDataBinding.bgaRlRecipe.postDelayed(() ->
                    get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, true, false), 1500);
        } else {
            get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, true, false);
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        if (mCurPage == mTotalPage) {
            Toast.makeText(viewDataBinding.getRoot().getContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            mCurPage++;
            mIsRefresh = false;
            get(ApiUtil.URL_RECIPE_LIST, RecipeListBean.class, false, false);
            return true;
        }
    }

    @Override
    public Map<Class<?>, Integer> getItemViewType()
    {
        Map<Class<?>, Integer> map = new HashMap<>();
        map.put(RecipeInfo.class, R.layout.item_recipe_list);
        return map;
    }

    @Override
    public SparseArray<Class<? extends BaseViewHolder>> getViewHolder()
    {
        SparseArray<Class<? extends BaseViewHolder>> sparseArray = new SparseArray<>();
        sparseArray.put(R.layout.item_recipe_list, RecipeListItemHolder.class);
        return sparseArray;
    }

    @Override
    public SparseArray<Class<? extends ViewDataBinding>> getViewDataBinding()
    {
        SparseArray<Class<? extends ViewDataBinding>> sparseArray = new SparseArray<>();
        sparseArray.put(R.layout.item_recipe_list, ItemRecipeListBinding.class);
        return sparseArray;
    }

    @Override
    public void emitter(Observable<Object> observable)
    {
        //lambda
        observable.subscribe((o -> {
            if (o instanceof Map) {
                Map map = (Map) o;
                int position = Integer.parseInt(map.get("position").toString());
                Toast.makeText(viewDataBinding.getRoot().getContext(), "position:" + position + " 你点击了" + mRecipeList.get(position).name + "的图片!", Toast.LENGTH_SHORT).show();
            }
        }));
    }


}

