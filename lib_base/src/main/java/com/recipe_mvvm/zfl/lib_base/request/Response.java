package com.recipe_mvvm.zfl.lib_base.request;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.recipe_mvvm.zfl.lib_base.mvvm.IBaseViewModel;
import com.recipe_mvvm.zfl.lib_base.utils.JsonUtil;
import com.recipe_mvvm.zfl.lib_base.utils.LogUtil;
import com.recipe_mvvm.zfl.lib_base.utils.MethodUtil;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.HttpException;

/**
 * @Description
 * @Author ZFL
 * @Date 2017/6/14.
 */

public class Response
{
    //用于转化的实体类
    private Class clazz;

    private Gson gson;

    private IBaseViewModel viewModel;

    private String wrongMsg;

    private Consumer<ResponseBody> response;

    private Consumer<Throwable> throwable;

    private Action complete;

    public Response(@NonNull Class clazz,@NonNull IBaseViewModel viewModel) {
        this.clazz = clazz;
        this.viewModel = viewModel;
        gson = new Gson();
        initConsumer();
    }

    private void initConsumer()
    {
        //lambda 语法
        response = (responseBody) -> {
            try
            {
                String json = responseBody.string();
                //                    LogUtil.e(json);
                if (JsonUtil.checkResult(json)) {
                    //请求成功
                    //进行数据解析，并且将具体数据通过反射机制返回给viewModel
                    MethodUtil.getMethod(viewModel, viewModel.getInvokeCallBackMap().get(clazz), clazz).invoke(viewModel, gson.fromJson(json, clazz));
                } else {
                    //请求失败
                    viewModel.error(JsonUtil.getMessage(json), JsonUtil.getCode(json));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                viewModel.error("反正又特么出错了:" + e.getMessage(), -1);
            } finally
            {
                viewModel.hideLoading();
            }
        };
        //lambda 语法
        throwable = (throwable) -> {
            if (throwable instanceof HttpException) {
                HttpException exception = (HttpException) throwable;
                int code = exception.code();
                wrongMsg = exception.getMessage();
                //后续会添加错误码的具体判断
                viewModel.error(wrongMsg, code);
            } else {
                viewModel.error(throwable.getMessage(), -1);
            }
            viewModel.hideLoading();
        };

        //lambda 语法
        complete = () -> LogUtil.v("complete");

    }

    public Consumer<ResponseBody> getResponse()
    {
        return response;
    }

    public Consumer<Throwable> getThrowable()
    {
        return throwable;
    }

    public Action getComplete()
    {
        return complete;
    }
}
