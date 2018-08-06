package com.recipe_mvvm.zfl.lib_res.widget.adapter;

import android.view.View;

/**
 * @Description 处理databinding的onclick事件，界面的click事件一律传入这个
 * @Author ZFL
 * @Date 2018/7/30
 */
public interface OnClickHandler
{
    void onRootClick(View view);

    void onRootLongClick(View view);

    void onChildClick(View view);

    void onChildLongClick(View view);
}
