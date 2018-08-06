package com.recipe_mvvm.zfl.lib_res.widget.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recipe_mvvm.zfl.lib_base.recipe_entity.RecipeInfo;
import com.recipe_mvvm.zfl.lib_base.utils.BGUtil;
import com.recipe_mvvm.zfl.lib_base.utils.ScreenUtil;
import com.recipe_mvvm.zfl.lib_res.R;


/**
 * @Description 目前只有添加label这一函数
 * @Author ZFL
 * @Date 2018/8/2
 */
public class LabelUtil
{
    /**
     * 给指定的viewGroup添加标签textView
     * @param context
     * @param viewGroup
     * @param text
     */
    public static void addLabelView(Context context, ViewGroup viewGroup, String text, float textSize) {
        TextView textView = new TextView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(ScreenUtil.dp2px(context, 5f), 0, 0, 0);
        textView.setLayoutParams(lp);
        int leftOrRightPadding = ScreenUtil.dp2px(context, 5f);
        int topOrBottomPadding = ScreenUtil.dp2px(context, 1f);
        textView.setPadding(leftOrRightPadding, topOrBottomPadding, leftOrRightPadding, topOrBottomPadding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        textView.setText(text);
        textView.setBackground(BGUtil.gradientDr(context, ScreenUtil.dp2px(context, 0.5f), 10f, R.color.colorPrimary, R.color.white));
        viewGroup.addView(textView);
    }

    /**
     * 用于dataBinding，简化代码。目前只用于list的item添加label
     * @param layout
     * @param data
     */
    @BindingAdapter({"addLabelView", "labelNum"})
    public static void listItemAddLabelView(LinearLayout layout, RecipeInfo data, int labelNum) {
        layout.removeAllViews();
        String[] recipeTypes = data.ctgTitles.split(",");
        //目前的标签数量
        int curLabelNum = 0;
        //如果有主标签，添加主标签
        if (!TextUtils.isEmpty(data.mainLabel)) {
            curLabelNum = 1;//主标签已经占据了一个位置
            addLabelView(layout.getContext(), layout, data.mainLabel, 13);
        }
        for (int i = 0; i < recipeTypes.length; i++) {
            if (!TextUtils.isEmpty(recipeTypes[i])) {
                if (!recipeTypes[i].equals(data.mainLabel)) {
                    addLabelView(layout.getContext(), layout, recipeTypes[i], 13);
                    curLabelNum++;
                }
            }
            if (curLabelNum == labelNum) break;//退出循环
        }
    }
}
