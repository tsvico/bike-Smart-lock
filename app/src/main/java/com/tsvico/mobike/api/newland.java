package com.tsvico.mobike.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class newland {
    private JSONObject object;
   /*String accessToken = "D18B227C42B019F4782E347914B4F9EDF54B9506692FC9EF6E79A76F6233" +
            "D754E94B4E23F7BE8C92013A541F8C54C23985" +
            "8000A010AACB8DD61AA6E1391A3F189694F046" +
            "2050641CF14F65CF311CCD457C79BD0BBE5A10" +
            "E5F107362BD843BF61C58776AE8E8FC0E2032A" +
            "45C14D51B35D0ED3375DD91EFAF6713208E9DA" +
            "478051737C0A4413E2E9765DD03A83FDB55B5A" +
            "B2909F1B3952A10C6C3E6AABBFFD160894C304" +
            "E65CEDC0B432277D9BBAFD32A8BDFD8A3D0877" +
            "C653D2CA2C2BE60BFA37F44B038AAA8D6C8549" +
            "A38BDA12114285428BA33E1A6B19CFA08246CC" +
            "1FC8EEED";*/

    public void setStatu(final String id, final String sign, final String type, final String accessToken){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = "http://www.nlecloud.com/cmds?deviceId="+id+"&apiTag="+sign;
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, type);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("Host", "api.nlecloud.com")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("Content-Length", "12")
                        .addHeader("AccessToken", accessToken)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "f51926a5-ebf6-5657-5b75-6fd832ca2fec")
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    //获取到数据
                    String date=response.body().string();
                    Log.e("TAG", "设置硬件状态------------>" + date);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public String getDate(final String Tag,String accessToken){
        final String[] resultt = {""};
        String url = "http://api.nlecloud.com/Devices/Datas?devIds=15809" +
                "&accesstoken="+accessToken + "&{}";
        final CountDownLatch latch = new CountDownLatch(1);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                latch.countDown();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String date = response.body().string();
                Log.e("TAG", "获取云平台回传json------------>" + date);
                if (date != null) {
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject jsonObject = new JSONObject(date);
                        //获取返回数据中flag的值
                        String resultCode = jsonObject.getString("Status");
                        //如果返回的值是success则正确
                        if (resultCode.equals("0")) {
                            //嵌套解析数据
                            String result = jsonObject.getString("ResultObj");
                            Log.e("result", "回传result------------>" + result);
                            JSONArray jsonArray = new JSONArray(result);
                            Log.e("result", "回传jsonArray------------>" + jsonArray.get(0));
                            //新大陆
                            String su = jsonArray.get(0).toString();
                            JSONObject jsonObject1 = new JSONObject(su);

                            JSONArray resultJsonArray = jsonObject1.getJSONArray("Datas");
                            //遍历

                            for (int i = 0; i < resultJsonArray.length(); i++) {
                                object = resultJsonArray.getJSONObject(i);
                                try {
                                    String ApiTag = object.getString("ApiTag");  //用户id
                                    String Value = object.getString("Value");  //头像
                                    String RecordTime = object.getString("RecordTime");   //姓名
                                    if(Tag.equals(ApiTag)){
                                        resultt[0] = Value;
                                        break;
                                    }else{
                                        resultt[0] = "没有结果";
                                    }
                                    Log.e("云平台ApiTag", "------------>" + ApiTag);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown(); //等一下
            }
        });
        try{
            latch.await(); //等待一下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("结果", "回传jsonArray------------>" + resultt[0]);
        return resultt[0];
    }

    public String[] getGps(final String Tag,final String Tag2,String accessToken){
        final String[] resultt = {"null","null"};
        String url = "http://api.nlecloud.com/Devices/Datas?devIds=15809" +
                "&accesstoken=" + accessToken+
                "&{}";
        final CountDownLatch latch = new CountDownLatch(1);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                latch.countDown();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String date = response.body().string();
                if (date != null) {
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject jsonObject = new JSONObject(date);
                        //获取返回数据中flag的值
                        String resultCode = jsonObject.getString("Status");
                        //如果返回的值是success则正确
                        if (resultCode.equals("0")) {
                            //嵌套解析数据
                            String result = jsonObject.getString("ResultObj");
                            //Log.e("result", "回传result------------>" + result);
                            JSONArray jsonArray = new JSONArray(result);
                            //Log.e("result", "回传jsonArray------------>" + jsonArray.get(0));
                            //新大陆
                            String su = jsonArray.get(0).toString();
                            JSONObject jsonObject1 = new JSONObject(su);

                            JSONArray resultJsonArray = jsonObject1.getJSONArray("Datas");
                            //遍历

                            for (int i = 0; i < resultJsonArray.length(); i++) {
                                object = resultJsonArray.getJSONObject(i);
                                try {
                                    String ApiTag = object.getString("ApiTag");  //用户id
                                    String Value = object.getString("Value");  //头像
                                    String RecordTime = object.getString("RecordTime");   //姓名
                                    if(Tag.equals(ApiTag)){
                                        resultt[0] = Value;
                                        //break;
                                    }
                                    if(Tag2.equals(ApiTag)){
                                        resultt[1] = Value;
                                        //break;
                                    }
                                    //Log.e("getDates", "------------>" + ApiTag);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                latch.countDown(); //等一下
            }
        });
        try{
            latch.await(); //等待一下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("结果", "回传jsonArray------------>" + resultt[0]+"---"+resultt[1]);
        return resultt;
    }

    public String Login(){
        final String[] accessToken = new String[1];
            final CountDownLatch latch = new CountDownLatch(1);
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "Account=18253800572&Password=han981014");
            Request request = new Request.Builder()
                    .url("http://api.nlecloud.com/Users/Login")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                String date = response.body().string();
                if (date != null) {
                    try {
                        //将字符串转换成jsonObject对象
                        JSONObject jsonObject = new JSONObject(date);
                        //获取返回数据中flag的值
                        int resultCode = jsonObject.getInt("Status");
                        //如果返回的值是success则正确
                        if (resultCode == 0) {
                            //嵌套解析数据
                            String result = jsonObject.getString("ResultObj");
                            //Log.e("result", "回传result------------>" + result);
                            JSONObject json = new JSONObject(result);
                            Log.e("新大陆","result  "+result);
                            String UserID = json.getString("UserID");  //用户id
                            Log.e("新大陆","UserID "+UserID);
                            String Token = json.getString("AccessToken");
                            accessToken[0] = Token;
                            //sparedCook.saveCookie(this,"Token",Token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                    latch.countDown(); //等一下
                }
            });
        try{
            latch.await(); //等待一下
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return accessToken[0];
    }

}
