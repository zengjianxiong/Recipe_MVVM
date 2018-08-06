package com.recipe_mvvm.zfl.lib_base.utils;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/12.
 */

public class MethodUtil
{
    /**
     * 根据方法名和方法参数，强行获取方法
     * @param invokeObject
     * @param methodName
     * @param invokeParamClass
     */
    public static Method getMethod(@NonNull Object invokeObject, @NonNull String methodName, Class... invokeParamClass) throws NoSuchMethodException, IllegalAccessException
    {
        Class invokeClass = invokeObject.getClass();
        return invokeClass.getMethod(methodName, invokeParamClass);
    }

}
