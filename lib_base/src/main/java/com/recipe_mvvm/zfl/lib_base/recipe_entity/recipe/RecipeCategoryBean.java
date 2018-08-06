package com.recipe_mvvm.zfl.lib_base.recipe_entity.recipe;



import com.recipe_mvvm.zfl.lib_base.mvvm.m.CommonResultBean;

import java.util.List;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/20.
 */

public class RecipeCategoryBean extends CommonResultBean
{
    public ResultBean result;

    public static class ResultBean
    {
        public CategoryInfoBean categoryInfo;
        public List<ChildsBean> childs;
    }

    public static class ChildsBean {
        public CategoryInfoBean categoryInfo;
        public List<ChildsBean> childs;
    }

    public static class CategoryInfoBean {
        public String ctgId;
        public String name;
        public String parentId;
    }

    @Override
    public String toString()
    {
        StringBuilder targetStrBuilder = new StringBuilder();
        targetStrBuilder.append(result.categoryInfo.name + "\n");
        for (ChildsBean bean : result.childs) {
            targetStrBuilder.append(bean.categoryInfo.name + "\n");
            for (ChildsBean bean1 : bean.childs) {
                targetStrBuilder.append(bean1.categoryInfo.name + "\n");
            }
        }
        return targetStrBuilder.toString();
    }
}
