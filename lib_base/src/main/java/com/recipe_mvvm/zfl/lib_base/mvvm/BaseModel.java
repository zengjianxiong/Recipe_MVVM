package com.recipe_mvvm.zfl.lib_base.mvvm;


import com.recipe_mvvm.zfl.lib_base.request.Response;
import com.recipe_mvvm.zfl.lib_base.request.RetrofitClient;

import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.Cache;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/12.
 */

public class BaseModel
{
    /**
     * 通用的请求get方法(不带中文参数)
     * @param url
     * @param map
     * @param cache
     * @param response
     * @return
     */
    public Disposable get(String url, Map<String, Object> map, Cache cache, Response response) {
        return RetrofitClient.getInstance().get(url, map, cache).subscribe(response.getResponse(), response.getThrowable(), response.getComplete());
    }

    /**
     * 通用的请求post方法(不带中文参数)
     * @param url
     * @param map
     * @param response
     * @return
     */
    public Disposable post(String url, Map<String, Object> map, Response response) {
        return RetrofitClient.getInstance().post(url, map).subscribe(response.getResponse(), response.getThrowable(), response.getComplete());
    }

    /**
     * 通用的请求post方法(带中文参数)
     * @param url
     * @param map
     * @param response
     * @return
     */
    public Disposable postWithChParam(String url, Map<String, Object> map, Response response) {
        return RetrofitClient.getInstance().postWithChParam(url, map).subscribe(response.getResponse(), response.getThrowable(), response.getComplete());
    }
}
