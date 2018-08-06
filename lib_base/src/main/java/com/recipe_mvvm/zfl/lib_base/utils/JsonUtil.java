package com.recipe_mvvm.zfl.lib_base.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description 初步判定返回的Json信息
 * @Author ZFL
 * @Date 2017/6/14.
 */

public class JsonUtil
{
    //这里返回Json字符串里一些关键信息的字段名，可以视具体的返回数据进行改变
    static final String MSG = "msg";
    //RET的值决定着这次请求是否成功，具体值由返回数据决定
    static final String RET = "retCode";
    //具体成功值由具体返回数据决定
    static final int SUCCESS_RET_CODE = 200;

    public static boolean checkResult(String json) {
        boolean b= false;
        try
        {
            JSONObject object = new JSONObject(json);
            if (object.has(RET)) {
                b = object.optInt(RET) == SUCCESS_RET_CODE;
            } else {
                b = false;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            b = false;
        }
        return b;
    }

    public static String getMessage(String json) {
        String message;
        try
        {
            JSONObject object = new JSONObject(json);
            if (object.has(MSG)) {
                message = object.optString(MSG);
            } else {
                //这里暂时不确定是什么信息
                message = "";
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            message = "反正就是出错了";
        }
        return message;
    }

    public static int getCode(String json) {
        int code = -1;
        try
        {
            JSONObject object = new JSONObject(json);
            if (object.has(RET)) {
                code = object.optInt(RET);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return code;
    }

}
