package com.recipe_mvvm.zfl.lib_base.utils;

import android.util.Log;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/27.
 */

public class StringUtil
{
    /**
     * 根据给定的textView，字符串，最大显示行数，返回截取后的字符串<br>
     * 显示效果：maxLine行，最后一行只显示一半。maxline必须大于或等于2
     * 类似效果(maxLine为2)：<br>
     * 啊啊啊啊啊啊<br>
     * 啊啊啊...
     */
    public static String getSubString(TextView tv, String content, int maxLine)
    {
        float width = tv.getPaint().measureText(content);
        float tvWidth = tv.getMeasuredWidth();
        Log.e("tvTest", "text width:" + width);
        Log.e("tvTest", "textView width:" + tvWidth);
        if (width < tvWidth * (maxLine - 1 + 0.5))
            return content;
        else
        {
            int sublength = (int) (tvWidth * (maxLine - 1 + 0.5) * content.length() / width);
            return content.substring(0, sublength) + "...";
        }
    }

    /**
     * 尝试把中文字符串转成UTF-8？
     * @param str
     * @return
     */
    public static String cnToUTF8(String str)
    {
        try
        {
            String targetStr = new String(str.getBytes("UTF-8"));
            return targetStr;
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
