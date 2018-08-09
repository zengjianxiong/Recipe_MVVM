package com.zfl.recipe_mvvm;

import com.lsxiao.apollo.core.Apollo;
import com.lsxiao.apollo.generate.RecipeFavorGeneratorImpl;
import com.lsxiao.apollo.generate.RecipeGeneratorImpl;
import com.recipe_mvvm.zfl.lib_base.BaseApplication;

public class RecipeApplication extends BaseApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        //给Apollo加入BinderGeneratorImpl
        Apollo.addApolloBinderGeneratorImpl(RecipeGeneratorImpl.instance());
        Apollo.addApolloBinderGeneratorImpl(RecipeFavorGeneratorImpl.instance());
    }
}
