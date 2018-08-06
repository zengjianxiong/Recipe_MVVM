package com.recipe_mvvm.zfl.lib_base.utils;

/**
 * @Description 存储着相关api网址和key
 * @Author ZFL
 * @Date 2017/6/21.
 */

public class ApiUtil
{
    //本人申请的AppKey
    public static final String APPKEY = "1397d7385ce22";

    public static final String URL_RECIPE_CATEGORY = "http://apicloud.mob.com/v1/cook/category/query";

    public static final String URL_RECIPE_LIST = "http://apicloud.mob.com/v1/cook/menu/search";

    public static final String URL_RECIPE_DETAIL = "http://apicloud.mob.com/v1/cook/menu/query";

    public static final String URL_ADD_FAVOR_RECIPE = "http://192.168.1.212:9090/zfl_recipe/recipe/insertFavorRecipe";

    public static final String URL_DELETE_FAVOR_RECIPE = "http://192.168.1.212:9090/zfl_recipe/recipe/deleteFavorRecipe";

    public static final String URL_IS_FAVOR_RECIPE = "http://192.168.1.212:9090/zfl_recipe/recipe/isFavorRecipe";

    public static final String URL_GET_FAVOR_RECIPE_LIST = "http://192.168.1.212:9090/zfl_recipe/recipe/getAllFavorRecipe";
}
