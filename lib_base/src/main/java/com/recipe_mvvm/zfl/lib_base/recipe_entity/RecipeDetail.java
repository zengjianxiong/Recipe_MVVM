package com.recipe_mvvm.zfl.lib_base.recipe_entity;

import com.google.gson.Gson;

import java.util.List;

/**
 * @Description 因为要使用greenDao，所以将一些entity类细分出来使用
 * @Author ZFL
 * @Date 2017/7/21.
 */

public class RecipeDetail
{
    public String img;
    public String ingredients;
    public String method;
    public MethodBean methodBean;//这个需要method字符串来进行转换
    public String sumary;
    public String title;

    /**
     * 这个方法是用在详细页面的，列表页没有必要进行转换
     */
    public void convertMethodBean() {
        Gson gson = new Gson();
        methodBean = gson.fromJson("{\"list\":" + method + "}", MethodBean.class);
    }

    public static class MethodBean {

        public List<MethodDetailBean> list;

        public static class MethodDetailBean {
            public String img;
            public String step;
        }

    }
}
