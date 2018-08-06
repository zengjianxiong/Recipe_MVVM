package com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe_list;


import com.recipe_mvvm.zfl.lib_base.mvvm.m.CommonResultBean;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;

import java.util.List;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/22.
 */

public class RecipeListBean extends CommonResultBean
{
    public RecipeListResultBean result;

    public static class RecipeListResultBean {
        public int curPage;
        public int total;
        public List<RecipeInfo> list;
    }

}
