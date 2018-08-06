package com.recipe_mvvm.zfl.lib_base.mvvm;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutResID());
        mViewModel = getViewModel();
        if (mViewModel != null) {
            getLifecycle().addObserver(mViewModel);
        }
        if (isNeedEventBus()) {
            EventBus.getDefault().register(this);
        }
        mContext = this;
        initData();
        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (isNeedEventBus()) {
            EventBus.getDefault().unregister(this);
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

    /**
     * Activity是否需要注册EventBus，如果不需要注册，请返回false，否则会导致EventBus注解错误！
     * @return true代表需要注册EventBus，false代表不需要注册EventBus
     */
    protected abstract boolean isNeedEventBus();
}
