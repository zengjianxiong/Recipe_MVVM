package com.recipe_mvvm.zfl.lib_res.widget.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/27.
 */

public class CommonRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder>
{
    protected List mDatas;
    protected Context mContext;
    protected AdapterTemplate mTemplate;
    protected AdapterEmitter mAdapterEmitter;
    /**
     * 构造方法，需要响应式监听数据，所有参数不允许为空
     * @param context 上下文
     * @param datas 数据集
     * @param template item模板接口
     * @param adapterEmitter 回调给RxAndroid的接口
     */
    public CommonRecyclerViewAdapter(@NonNull Context context, @NonNull List datas, @NonNull AdapterTemplate template, @NonNull AdapterEmitter adapterEmitter){
        mContext = context;
        mDatas = datas;
        mTemplate = template;
        mAdapterEmitter = adapterEmitter;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mTemplate.getItemViewType().get(mDatas.get(position).getClass());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        try
        {
            return mTemplate.getViewHolder().get(viewType).getConstructor(mTemplate.getViewDataBinding().get(viewType)).newInstance
                    (DataBindingUtil.inflate(LayoutInflater.from(mContext), viewType, parent, false));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position)
    {
        holder.bindViewHolder(mDatas.get(position), position);
        if (mAdapterEmitter != null) {
            mAdapterEmitter.emitter(holder.getObservable());
        }
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }


    public interface AdapterTemplate
    {
        /**
         * @return map, class is entity class , integer is layoutId
         */
        Map<Class<?>,Integer> getItemViewType();

        /**
         * @return SparseArray, class is what extents BaseViewHolder
         */
        SparseArray<Class<? extends BaseViewHolder>> getViewHolder();

        /**
         * @return SparseArray, class is what extends ViewDataBinding
         */
        SparseArray<Class<? extends ViewDataBinding>> getViewDataBinding();
    }
    public interface AdapterEmitter
    {
        /**
         * Rx Observable, Integer was position
         */
        void emitter(Observable<Object> observable);

    }

}
