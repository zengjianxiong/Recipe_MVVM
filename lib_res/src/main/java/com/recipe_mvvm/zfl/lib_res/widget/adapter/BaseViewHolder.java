package com.recipe_mvvm.zfl.lib_res.widget.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * @Description
 * @Author MoseLin
 * @Date 2016/7/21.
 */

public abstract class BaseViewHolder<T, VB extends ViewDataBinding> extends RecyclerView.ViewHolder
        implements OnClickHandler
{
    protected Context context;
    protected Observable<Object> observable;
    protected ObservableEmitter<Object> emi;

    protected VB mViewDataBing;

    public BaseViewHolder(VB viewDataBing)
    {
        super(viewDataBing.getRoot());
        context = viewDataBing.getRoot().getContext();
        mViewDataBing = viewDataBing;
        createObservable();
    }

    /**
     * 因为根view的点击处理事件也在这里进行，所以必须初始化
     */
    protected void createObservable(){
        //lambda 语法
        observable = Observable.create((obEmitter) -> emi = obEmitter);

    }

    public  Observable<Object> getObservable(){
        return observable;
    }

    public void bindViewHolder(T data, int position) {
        childBindViewHolder(data, position);
        mViewDataBing.executePendingBindings(); //防止绑定view时会进行界面闪烁
    }

    public abstract void childBindViewHolder(T data, int position);

}
