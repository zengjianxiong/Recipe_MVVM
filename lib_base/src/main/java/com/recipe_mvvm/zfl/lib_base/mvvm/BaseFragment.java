package com.recipe_mvvm.zfl.lib_base.mvvm;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Description 基类Fragment，实现懒加载
 * @Author ZFL
 * @Date 2018/7/26
 */
public abstract class BaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends Fragment
{

    private boolean isPrepared = false;//证明fragment界面元素是否已经加载完毕

    private boolean isFirstVisible = true;//判断是否是第一次可见，避免重复加载数据

    protected VM mViewModel;

    protected DB mViewDataBinding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutResID(), container, false);
        mViewModel = getViewModel();
        initView();
        return mViewDataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;
        initData();
        //处理第一个fragment的逻辑。必须在界面搭建完成后才去请求数据，不然会造成空指针错误
        if (getUserVisibleHint()) {
            //这里代表是第一个fragment
            lazyLoad();
            isFirstVisible = false;
            //同时设置第一个fragment的可见监听器的可见状态
            if (mVisibleHintListener != null) mVisibleHintListener.onUserVisibleHint(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared && isFirstVisible) {
            //当页面搭建完成，处于可见状态并且是第一次处于可见状态，开始加载数据
            lazyLoad();
            isFirstVisible = false;
        }
        //子类Fragment或者其他类想处理Fragment可见不可见逻辑，请实现此接口
        if (mVisibleHintListener != null) mVisibleHintListener.onUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        isPrepared = false;
        isFirstVisible = true;
    }
    /**
     * 懒加载的函数，具体实现看具体页面而定
     */
    protected abstract void lazyLoad();
    /**
     * 获取viewModel，可以在这个函数进行viewBinding，将viewBinding对象传入viewModel，进行数据页面绑定
     * @return
     */
    protected abstract VM getViewModel();
    /**
     * 获取该Fragment的界面视图id
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化data，由具体Fragment实现
     */
    protected abstract void initData();

    /**
     * 初始化view，由具体Fragment实现
     */
    protected abstract void initView();

    private OnUserVisibleHintListener mVisibleHintListener;

    public void setVisibleHintListener(OnUserVisibleHintListener listener) {
        mVisibleHintListener = listener;
    }
    /**
     * 基类Fragment可见或者不可见时触发的回调接口<br>
     * 子类Fragment如果需要处理可见不可见逻辑，实现此接口
     */
    public interface OnUserVisibleHintListener{
        void onUserVisibleHint(boolean isVisibleToUser);
    }
}
