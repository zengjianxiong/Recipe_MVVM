package com.zfl.recipe_mvvm;

import com.recipe_mvvm.zfl.RecipeFavorIndex;
import com.recipe_mvvm.zfl.RecipeIndex;
import com.recipe_mvvm.zfl.lib_base.BaseApplication;

import org.greenrobot.eventbus.EventBus;

public class RecipeApplication extends BaseApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        //初始化EventBus的Index
        EventBus.builder()
                .addIndex(new RecipeIndex())
                .addIndex(new RecipeFavorIndex())
                .installDefaultEventBus();
    }
}
