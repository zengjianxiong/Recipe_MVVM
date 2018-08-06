package com.recipe_mvvm.zfl.lib_res.widget.holder;

import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;
import com.recipe_mvvm.zfl.lib_base.utils.ARouterPathUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ConstantUtil;
import com.recipe_mvvm.zfl.lib_res.R;
import com.recipe_mvvm.zfl.lib_res.databinding.ItemRecipeListBinding;
import com.recipe_mvvm.zfl.lib_res.widget.adapter.BaseViewHolder;

import java.util.HashMap;
import java.util.Map;

public class RecipeListItemHolder extends BaseViewHolder<RecipeInfo, ItemRecipeListBinding>
{

    private static final int MAX_LABEL_NUM = 5;//最多添加5个标签

    private RecipeInfo mRecipeInfo;

    public RecipeListItemHolder(ItemRecipeListBinding viewDataBing)
    {
        super(viewDataBing);
        mViewDataBing.setClickHandler(this);
    }

    @Override
    public void childBindViewHolder(RecipeInfo data, int position)
    {
        mRecipeInfo = data;
        mViewDataBing.setRecipeInfo(data);
        mViewDataBing.setLabelNum(MAX_LABEL_NUM);
    }

    @Override
    public void onRootClick(View view)
    {
        //前往详情页
        ARouter.getInstance()
                .build(ARouterPathUtil.RecipeDetailActivity)
                .withString(ConstantUtil.RECIPE_MENU_ID, mRecipeInfo.menuId)
                .navigation();
    }

    @Override
    public void onRootLongClick(View view)
    {

    }

    @Override
    public void onChildClick(View view)
    {
        if (view.getId() == R.id.ivRecipeListItemThumbnail)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("position", getAdapterPosition());
            emi.onNext(map);
        }
    }

    @Override
    public void onChildLongClick(View view)
    {

    }
}


