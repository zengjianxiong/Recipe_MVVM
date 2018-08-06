package com.recipe_mvvm.zfl.lib_base.recipe_entity;


import com.recipe_mvvm.zfl.lib_base.mvvm.m.CommonResultBean;

/**
 * @Description 专门用于查询是否是已经加入收藏菜谱的bean
 * @Author ZFL
 * @Date 2017/8/12.
 */
public class RecipeFavorBean extends CommonResultBean
{
    public boolean isFavor;
//    public String name;
    public int recipeEventType;
    public String menuId;
}
