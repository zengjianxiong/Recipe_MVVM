package com.recipe_mvvm.zfl.lib_base.request;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @Description 后面可以视情况，添加上传，下载api
 * @Author ZFL
 * @Date 2017/6/12.
 */

public interface IRetrofitApi
{
    /**
     * 发起get请求(不带中文参数)
     * @param url action路径
     * @param parameters get参数
     * @return Observable
     */
    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, Object> parameters);

    /**
     * 发起post请求(带中文参数)
     * @param url action路径
     * @param parameters post参数
     * @return Observable
     */
    @FormUrlEncoded
    @POST()
    Observable<ResponseBody> postWithChParam(@Url String url, @FieldMap Map<String, Object>
            parameters);

    /**
     * 发起post请求(不带中文参数)
     * @param url
     * @param parameters
     * @return
     */
    @POST()
    Observable<ResponseBody> post(@Url String url, @QueryMap Map<String, Object> parameters);
}
