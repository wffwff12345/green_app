package com.example.green_app.http;



import com.example.green_app.model.Result;

import java.io.IOException;

public interface OnHttpCallback {
    void onFailure(IOException e, int id, int error);

    void onResponse(Result result, int id);
}