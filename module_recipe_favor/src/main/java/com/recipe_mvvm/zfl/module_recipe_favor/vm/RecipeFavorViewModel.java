package com.recipe_mvvm.zfl.module_recipe_favor.vm;

import android.app.ProgressDialog;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.widget.Toast;

import com.recipe_mvvm.zfl.lib_base.mvvm.BaseViewModel;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe_list.RecipeListBean;
import com.recipe_mvvm.zfl.lib_base.utils.ApiUtil;
import com.recipe_mvvm.zfl.lib_res.databinding.ItemRecipeListBinding;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.BaseViewHolder;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.CommonRecyclerViewAdapter;
import com.recipe_mvvm.zfl.lib_res.widget.holder.RecipeListItemHolder;
import com.recipe_mvvm.zfl.module_recipe_favor.R;
import com.recipe_mvvm.zfl.module_recipe_favor.databinding.ActivityRecipeFavorBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class RecipeFavorViewModel extends BaseViewModel<ActivityRecipeFavorBinding> implements BGARefreshLayout.BGARefreshLayoutDelegate,
        CommonRecyclerViewAdapter.AdapterTemplate, CommonRecyclerViewAdapter.AdapterEmitter
{

    private ProgressDialog mLoadingDialog;

    //适配器
    private CommonRecyclerViewAdapter mAdapter;

    private List<RecipeInfo> mList;

    private boolean mIsLazyLoad = false;//判断是否是懒加载

    public RecipeFavorViewModel(ActivityRecipeFavorBinding viewDataBinding)
    {
        super(viewDataBinding);
    }

    public void setAdapter(CommonRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    public void setList(List<RecipeInfo> list) {
        mList = list;
    }

    @Override
    protected void start()
    {
        mIsLazyLoad = true;
        viewDataBinding.bgaRlFavorRecipe.beginRefreshing();
    }

    @Override
    protected void clear()
    {

    }

    public void updateFavorList(RecipeListBean bean)
    {
        //在请求成功后，不管是不是懒加载，都可以将mIsLazyLoad布尔值设置为false，因为懒加载只执行一次
        mIsLazyLoad = false;
        mList.clear();
        mList.addAll(bean.result.list);
        mAdapter.notifyDataSetChanged();
        viewDataBinding.bgaRlFavorRecipe.endRefreshing();
    }

    @Override
    public Map<Class, String> getInvokeCallBackMap()
    {
        Map<Class, String> map = new HashMap<>();
        map.put(RecipeListBean.class, "updateFavorList");
        return map;
    }

    @Override
    public Map<Class, Map<String, Object>> getParamsMap()
    {
        Map<Class, Map<String, Object>> map = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("testCNStr", "中文字符串测试");
        map.put(RecipeListBean.class, paramMap);
        return map;
    }

    @Override
    public void showLoading()
    {
        mLoadingDialog = ProgressDialog.show(viewDataBinding.getRoot().getContext(), "请求收藏菜谱数据", "请求中......", true, false);
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
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout)
    {
        if (mIsLazyLoad) {
            refreshLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    get(ApiUtil.URL_GET_FAVOR_RECIPE_LIST, RecipeListBean.class, false, false);
                }
            }, 1500);//延迟1.5秒（至少让用户能看见完整的动画啊）
        } else {
            get(ApiUtil.URL_GET_FAVOR_RECIPE_LIST, RecipeListBean.class, false, false);
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout)
    {
        return false;
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
        observable.subscribe(new Consumer<Object>()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                if (o instanceof Map) {
                    Map map = (Map) o;
                    int position = Integer.parseInt(map.get("position").toString());
                    Toast.makeText(viewDataBinding.getRoot().getContext(), "position:" + position + " 你点击了" + mList.get(position).name + "的图片!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
