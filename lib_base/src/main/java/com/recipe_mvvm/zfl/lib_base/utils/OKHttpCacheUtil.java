package com.recipe_mvvm.zfl.lib_base.utils;

import android.content.Context;

import java.io.File;

import okhttp3.Cache;

/**
 * @Description 为OkHttp缓存提供可以选择的cache
 * @Author ZFL
 * @Date 2017/6/12.
 */

public class OKHttpCacheUtil
{
    //缓存大小默认是20M
    private static final int DEFAULT_CACHE_SIZE = 1024 * 1024 * 20;

    /**
     * 使用默认的缓存
     * @param context
     * @return
     */
    public static Cache defaultCache(Context context) {
        //http cache path设置缓存路径
        File cacheFile = new File(context.getExternalCacheDir(), "retrofit");
        return cache(cacheFile.getPath(), DEFAULT_CACHE_SIZE);
    }

    /**
     * 使用指定路径的缓存（缓存大小默认）
     * @param cachePath
     * @return
     */
    public static Cache cache(String cachePath) {
        return cache(cachePath, DEFAULT_CACHE_SIZE);
    }

    /**
     * 使用指定路径和指定大小的缓存
     * @param cachePath
     * @param cacheSize
     * @return
     */
    public static Cache cache(String cachePath, int cacheSize) {
        File cacheFile = new File(cachePath);
        Cache cache = new Cache(cacheFile, cacheSize);
        return cache;
    }

    /**
     * 不使用缓存
     * @return
     */
    public static Cache noCache() {
        return null;
    }

}
