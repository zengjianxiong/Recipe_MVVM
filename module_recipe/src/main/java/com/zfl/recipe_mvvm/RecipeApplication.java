package com.zfl.recipe_mvvm;

import com.lsxiao.apollo.core.Apollo;
import com.recipe_mvvm.zfl.lib_base.BaseApplication;

public class RecipeApplication extends BaseApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        //给Apollo加入BinderGeneratorImpl
        Apollo.addApolloBinderGeneratorImpl("ApolloBinderGenerator_module_recipe_Impl");
        Apollo.addApolloBinderGeneratorImpl("ApolloBinderGenerator_module_recipe_favor_Impl");
    }
}
