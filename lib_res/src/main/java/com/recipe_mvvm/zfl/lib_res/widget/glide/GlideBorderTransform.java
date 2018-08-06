package com.recipe_mvvm.zfl.lib_res.widget.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * @Description 给Glide加载的图片加上一层边框
 * @Author ZFL
 * @Date 2017/7/3.
 */

public class GlideBorderTransform extends BitmapTransformation
{

    private static final String ID = "com.bumptech.glide.transformations.GlideBorderTransform";

    private Paint mBorderPaint;
    private int mBorderWidth;
    private int mBorderColorResId;

    /**
     * 边框宽度和颜色id不能为0
     * @param context
     * @param borderWidth
     * @param borderColorResId
     */
    public GlideBorderTransform(Context context, int borderWidth, int borderColorResId)
    {
        super();
        int borderColor = context.getResources().getColor(borderColorResId);
//        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderPaint = new Paint();
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(borderWidth);
        mBorderWidth = borderWidth;
        mBorderColorResId = borderColorResId;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight)
    {
        int targetWidth = toTransform.getWidth() + mBorderWidth * 2;
        int targetHeight = toTransform.getHeight() + mBorderWidth * 2;
        Bitmap result = pool.get(targetWidth, targetHeight, toTransform.getConfig());
        if (result == null) {
            result = Bitmap.createBitmap(targetWidth, targetHeight, toTransform.getConfig());
        }
        //创建目标图片的画布
        Canvas canvas = new Canvas(result);
        //先画原始图片在正中央
        Paint paint = new Paint();
        canvas.drawBitmap(toTransform, mBorderWidth, mBorderWidth, paint);
        //画边框(上下左右)
        canvas.drawLine(0, 0, targetWidth, 0, mBorderPaint);
        canvas.drawLine(0, targetHeight, targetWidth, targetHeight, mBorderPaint);
        canvas.drawLine(0, 0, 0, targetHeight, mBorderPaint);
        canvas.drawLine(targetWidth, 0, targetWidth, targetHeight, mBorderPaint);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof GlideBorderTransform) {
            GlideBorderTransform other = (GlideBorderTransform) obj;
            if (mBorderWidth != other.mBorderWidth) return false;
            if (mBorderColorResId != other.mBorderColorResId) return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Util.hashCode(ID.hashCode(), Util.hashCode(mBorderWidth, mBorderColorResId));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest)
    {
        try
        {
            messageDigest.update(ID.getBytes(Key.STRING_CHARSET_NAME));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        byte[] widthData = ByteBuffer.allocate(4).putFloat(mBorderWidth).array();
        messageDigest.update(widthData);
        byte[] colorResData = ByteBuffer.allocate(4).putInt(mBorderColorResId).array();
        messageDigest.update(colorResData);
    }
}
