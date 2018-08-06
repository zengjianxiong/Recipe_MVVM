package com.recipe_mvvm.zfl.lib_base.mvvm;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ViewDataBinding;
import android.widget.Toast;

import com.recipe_mvvm.zfl.lib_base.request.Response;
import com.recipe_mvvm.zfl.lib_base.utils.OKHttpCacheUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import okhttp3.Cache;

/**
 * @Description 基础的viewmodel。viewmodel作用：完成数据绑定，以及相关业务逻辑处理<br>
 *     实现它的子类要实现单例模式，以便实现注解BindingAdapter的函数
 * @Author ZFL
 * @Date 2018/7/26
 */
public abstract class BaseViewModel<VDB extends ViewDataBinding> implements LifecycleObserver, IBaseViewModel
{

    protected VDB viewDataBinding;

    //start函数通常只调用一次，此布尔值是为了保证解锁，切换应用时start函数不被调用多次
    protected boolean mStartAlready = false;

    //为了能够统一管理正在进行的后台任务
    private CompositeDisposable dis = new CompositeDisposable();

    protected BaseModel model;

    public BaseViewModel(VDB viewDataBinding)
    {
        this.viewDataBinding = viewDataBinding;
        model = new BaseModel();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        if (!mStartAlready)
        {
            start();
            mStartAlready = true;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        //取消后台任务，防止内存溢出
        dis.clear();
        hideLoading();
        clear();
    }

    /**
     * 较通用的get方法
     * @param url 网址
     * @param clazz 用于解析数据的实体类class
     * @param needCache 是否缓存
     */
    public void get(String url, Class clazz, boolean needCache, boolean isShowLoading) {
        if (isShowLoading) showLoading();
        Cache cache;
        //现在缓存cache由presenter决定
        if (needCache) {
            cache = OKHttpCacheUtil.defaultCache(viewDataBinding.getRoot().getContext());
        } else {
            cache = OKHttpCacheUtil.noCache();
        }
        addDis(model.get(url, getParamsMap().get(clazz), cache, new Response(clazz, this)));
    }

    /**
     * 较通用的post方法
     * @param url 网址
     * @param clazz 用于解析数据的实体类class
     */
    public void post(String url, Class clazz, boolean isShowLoading, boolean haveChParam) {
        if (isShowLoading) showLoading();
        Disposable disposable;
        if (haveChParam) {
            disposable = model.postWithChParam(url, getParamsMap().get(clazz), new Response(clazz, this));
        } else {
            disposable = model.post(url, getParamsMap().get(clazz), new Response(clazz, this));
        }
        addDis(disposable);
    }


    public void addDis(Disposable disposable) {
        dis.add(disposable);
    }

    @Override
    public void error(String msg, int code)
    {
        Toast.makeText(viewDataBinding.getRoot().getContext(), "msg:" + msg + "\ncode:" + code, Toast.LENGTH_SHORT).show();
    }

    /**
     * 在onResume函数中调用，由具体ViewModel实现<br>
     * 由具体页面决定，view搭建完成后，要去请求数据或者其他工作
     */
    protected abstract void start();

    /**
     * 在destroy中调用，由具体ViewModel实现<br>
     * 子类ViewModel来清理数据
     */
    protected abstract void clear();
}
