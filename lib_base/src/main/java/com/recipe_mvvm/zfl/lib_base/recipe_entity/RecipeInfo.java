package com.recipe_mvvm.zfl.lib_base.recipe_entity;

import java.util.List;

/**
 * @Description 现在已经不使用GreenDao了，所以讲注解移除
 * @Author ZFL
 * @Date 2017/7/21.
 */
public class RecipeInfo
{
    public String menuId;

    public String mainLabel;//主标签
    public String ctgTitles;
    public String name;
    public String thumbnail;

    public RecipeDetail recipe;
    public List<String> ctgIds;

    @Override
    public String toString()
    {
        String targetStr = "menuId:" + menuId + " mainLabel:" + mainLabel + " ctgTitles:" + ctgTitles + " name:" + name;
        return targetStr;
    }

}
