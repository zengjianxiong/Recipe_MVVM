package com.recipe_mvvm.zfl.lib_base.mvvm;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.core.contract.ApolloBinder;

/**
 * @Description 在这里进行一些跟数据不相关的view的初始化，与数据相关的view变化交给viewmodel处理
 * @Author ZFL
 * @Date 2018/7/27
 */
public abstract class BaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends AppCompatActivity
{

    protected Context mContext;

    protected VM mViewModel;

    protected DB mViewDataBinding;

    private ApolloBinder mApolloBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutResID());
        mViewModel = getViewModel();
        if (mViewModel != null) {
            getLifecycle().addObserver(mViewModel);
        }
        mApolloBinder = Apollo.bind(this); //现在有可能返回null值，解绑时必须判定是否为null
        mContext = this;
        initData();
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mApolloBinder != null) {
            mApolloBinder.unbind();
        }
    }
    /**
     * 获取viewModel
     * @return
     */
    protected abstract VM getViewModel();
    /**
     * 获取layoutID
     * @return
     */
    protected abstract int getLayoutResID();
    /**
     * 初始化data，一般是intent中的data。由具体Activity实现
     */
    protected abstract void initData();

    /**
     * 初始化view，由具体Activity实现
     */
    protected abstract void initView();

}
