package com.recipe_mvvm.zfl.lib_res.widget.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.recipe_mvvm.zfl.lib_base.utils.ScreenUtil;
import com.recipe_mvvm.zfl.lib_res.R;
import com.recipe_mvvm.zfl.lib_res.widget.glide.GlideRoundTransform;

/**
 * @Description Glide图片加载通用类，后续会添加更多的方法
 * @Author ZFL
 * @Date 2018/8/1
 */
public class GlideUtil
{

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view).load(url).into(view);
    }

    @BindingAdapter({"roundBorderImage"})
    public static void loadRoundBorderImage(ImageView view, String url) {
        Context context = view.getContext();
        RequestOptions requestOptions = new RequestOptions().transform(new GlideRoundTransform(context,
                ScreenUtil.dp2px(context, 3), ScreenUtil.dp2px(context, 0.5f), R.color.colorPrimary))
                .fallback(R.mipmap.default_recipe_img).placeholder(R.mipmap.default_recipe_img);
        Glide.with(context).load(url).apply(requestOptions).into(view);
    }
}
