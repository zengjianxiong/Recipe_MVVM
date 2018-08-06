package com.recipe_mvvm.zfl.lib_res.widget.glide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
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
 * @author Gxcheng
 * @desc    使用glide加载圆形图片
 * @time 2016/8/30 0030 11:52
 */
public class GlideCircleTransform extends BitmapTransformation
{

    private static final String ID = "com.bumptech.glide.transformations.GlideCircleTransform";

    private Paint mBorderPaint;
    private float mBorderWidth;

    private int mBorderColor;


    public GlideCircleTransform(int borderWidth, int borderColor) {
        super();
        mBorderWidth = Resources.getSystem().getDisplayMetrics().density * borderWidth;
        mBorderColor = borderColor;
        mBorderPaint = new Paint();
        mBorderPaint.setDither(true);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }


    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        if (mBorderPaint != null) {
            float borderRadius = r - mBorderWidth / 2;
            canvas.drawCircle(r, r, borderRadius, mBorderPaint);
        }
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof GlideCircleTransform) {
            GlideCircleTransform other = (GlideCircleTransform) obj;
            if (mBorderWidth != other.mBorderWidth) return false;
            if (mBorderColor != other.mBorderColor) return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Util.hashCode(ID.hashCode(), Util.hashCode(mBorderWidth, mBorderColor));
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
        byte[] colorData = ByteBuffer.allocate(4).putInt(mBorderColor).array();
        messageDigest.update(colorData);

    }
}
