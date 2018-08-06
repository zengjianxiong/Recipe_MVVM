package com.recipe_mvvm.zfl.lib_base.request;

import android.content.Context;

import com.recipe_mvvm.zfl.lib_base.request.okhttp.RequestCacheInterceptor;
import com.recipe_mvvm.zfl.lib_base.utils.NetWorkUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description 使用retrofit进行请求
 * @Author ZFL
 * @Date 2017/6/12.
 */

public class RetrofitClient
{

    private OkHttpClient.Builder mBuilder;

    public RetrofitClient(Context context)
    {
        mBuilder = new OkHttpClient().newBuilder()
                .addInterceptor(new RequestCacheInterceptor(context))
                .addNetworkInterceptor(new RequestCacheInterceptor(context))
                .connectTimeout(10, TimeUnit.SECONDS)//连接超时时间10S
                .readTimeout(10, TimeUnit.SECONDS) //读取超时时间设置10S
                .writeTimeout(10, TimeUnit.SECONDS);//写入超时时间设置10S
    }

    /**
     * 通用的get方法(不带中文参数)
     * @param url 获取数据地址
     * @param params 获取数据时传给服务器的参数
     * @param cache 缓存由用户自定义生成，可参照OKHttpCacheUtil类生成cache
     * @return
     */
    public Observable<ResponseBody> get(String url, Map<String, Object> params, Cache cache) {
        return getRetrofit(url, mBuilder.cache(cache).build())
                .create(IRetrofitApi.class)
                .get(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用的post方法(不带中文参数)
     * @param url post地址
     * @param params 传给服务器的参数
     * @return
     */
    public Observable<ResponseBody> post(String url, Map<String, Object> params) {
        return getRetrofit(url, mBuilder.build())
                .create(IRetrofitApi.class)
                .post(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通用的post方法(带中文参数)
     * @param url post地址
     * @param params 传给服务器的参数
     * @return
     */
    public Observable<ResponseBody> postWithChParam(String url, Map<String, Object> params) {
        return getRetrofit(url, mBuilder.build())
                .create(IRetrofitApi.class)
                .postWithChParam(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Retrofit getRetrofit(String url, OkHttpClient client) {
        String baseUrl = NetWorkUtil.getHostName(url);
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    private static RetrofitClient client;

    /**
     * 对唯一的Client进行初始化
     * @param context
     */
    public static void init(Context context) {
        client = new RetrofitClient(context);
    }

    public static RetrofitClient getInstance() {
        return client;
    }
}
