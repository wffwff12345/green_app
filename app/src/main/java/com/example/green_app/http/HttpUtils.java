package com.example.green_app.http;

import android.content.Context;

import androidx.annotation.NonNull;


import com.example.green_app.model.Result;
import com.example.green_app.utils.SPUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Http 工具类
 */
public class HttpUtils {
    public static final int ERROR_LOGIN = -1;
    public static final int ERROR_NORMAL = 100;
    public static final String AUTHORIZATION = "Authorization";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static OkHttpClient client;
    private static HttpUtils httpUtils;
    private String auth = null;

    private static OkHttpClient HttpClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static HttpUtils Instance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    public boolean setAuth(Context context) {
        String auth = SPUtils.get(SPUtils.AUTHORIZATION, context);
        if (auth != null) {
            this.auth = auth;
            return true;
        }
        return false;
    }

    public void login(OnHttpCallback callback, Object object, int callId, Context context) {
        String url = Constant.API.LOGIN.getUrl(context);
        String json = JsonUtils.toJson(object);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e, callId, ERROR_NORMAL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                String token = (String) result.get("token");
                SPUtils.set(SPUtils.AUTHORIZATION, "Bearer " + token, context);
                setAuth(context);
                callback.onResponse(result, callId);
            }
        });
    }

    public void postJson(String url, Object object, OnHttpCallback callback, int id, Context context) {
        if (auth == null) {
            if (!setAuth(context)) {
                callback.onFailure(new IOException("验证失败！"), id, ERROR_LOGIN);
                return;
            }
        }
        String json = JsonUtils.toJson(object);
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder().header(AUTHORIZATION, auth).url(url).post(requestBody).build();
        HttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e, id, ERROR_NORMAL);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                Result result = JsonUtils.toBean(response.body().string(), Result.class);
                if (401 == (Integer)(result.get("code"))) {
                    callback.onFailure(new IOException("验证失败"), id, ERROR_LOGIN);
                } else {
                    callback.onResponse(result, id);
                }

            }
        });
    }

}