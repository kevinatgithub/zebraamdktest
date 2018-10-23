package com.example.kevin.henobarcodetest;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterSave {

    private String code;
    private Context context;
    private RequestQueue queue;
    private final String URL = "http://test.nbbnets.net/v2/public/heno/register";

    public RegisterSave(Context context, String code) {
        this.context = context;
        this.code = code;

        queue = Volley.newRequestQueue(context);
    }

    public void save(){
        final String CODE = this.code;
        final Context CONTEXT = this.context;
        StringRequest postRequest = new StringRequest(Request.Method.POST, URL, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
                protected Map<String, String> getParams(){
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("code", CODE);
                    return params;
                }
            };

        this.queue.add(postRequest);
    }
}
