package com.recipe_mvvm.zfl.lib_base;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lsxiao.apollo.core.Apollo;
import com.recipe_mvvm.zfl.lib_base.request.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @Description 基础的Application类
 * @Author ZFL
 * @Date 2018/7/26
 */
public class BaseApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
        RetrofitClient.init(this);
        Apollo.init(AndroidSchedulers.mainThread(), this);
    }
}
