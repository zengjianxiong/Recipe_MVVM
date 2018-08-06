package com.recipe_mvvm.zfl.lib_base.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

/**
 * @Description 给组件View设置可以带弧度，特定边框，特定颜色的背景drawable
 * @Author ZFL
 * @Date 2017/6/27.
 */

public class BGUtil
{
    /**
     *  设置控件的边框，颜色，圆角（可自定义四个角度不同弧度），填充色，添加：朱飞龙
     * @param context
     * @param strokeWidth 边框宽度
     * @param radii 圆角
     * @param strokeColorCId 边框颜色
     * @param fillColorCId 内部填充颜色
     * @return
     */
    public static GradientDrawable gradientDr(Context context, int strokeWidth, float[] radii, int strokeColorCId, int fillColorCId) {
        GradientDrawable gd = new GradientDrawable();
        int fillColor = Color.parseColor(context.getResources().getString(fillColorCId));//内部填充颜色
        gd.setColor(fillColor);
        gd.setCornerRadii(radii);
        if (strokeWidth != 0) {
            int strokeColor = Color.parseColor(context.getResources().getString(strokeColorCId));//边框颜色
            gd.setStroke(strokeWidth, strokeColor);
        }
        return gd;
    }

    /**
     * 设置控件的边框，颜色，圆角（只能同时指定四个圆角），填充色，添加：朱飞龙
     * @param context
     * @param strokeWidth 边框宽度
     * @param radii 圆角（同时适用于四个圆角）
     * @param strokeColorCId 边框颜色
     * @param fillColorCId 内部填充颜色
     * @return
     */
    public static GradientDrawable gradientDr(Context context, int strokeWidth, float radii, int strokeColorCId, int fillColorCId) {
        float[] radiis = new float[]{radii, radii, radii, radii, radii, radii, radii, radii};
        return gradientDr(context, strokeWidth, radiis, strokeColorCId, fillColorCId);
    }
}
