package com.vCare.murlipurajaipurswm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAAWLxrVRI:APA91bE4GFtvRHlrde4wgqSzJXGdAMUfU2nif08f49yZaPiFuVXAOfCI8gFgJqpEwnHVpOIPpYwn45FBsjspPZsBzUjd6mJlpL8BXkJqp-Kv1hgiONqPDDXDbV_c_ouXZKfuSgnxsGZQ";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {
        Log.e("Notification sender", "call " + userFcmToken);
        requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();

            notiObject.put("title", "Kindly Pay the Pending Month's Payment to Avoid Penalty from Nagar Nigam Greater");
            notiObject.put("body", "Kindly Pay the Pending Month's Payment to Avoid Penalty from Nagar Nigam Greater");
            notiObject.put("android_channel_id", "Notification_Center");

            mainObj.put("notification", notiObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("TAG", "Response: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", "Volley Error: " + error.toString());

                    if (error.networkResponse != null) {
                        Log.e("TAG", "Status Code: " + error.networkResponse.statusCode);

                        try {
                            String responseBody = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            Log.e("TAG", "Error Response Body: " + responseBody);

                            // Log headers to inspect redirect information
                            Map<String, String> headers = error.networkResponse.headers;
                            for (String key : headers.keySet()) {
                                Log.e("TAG", "Header: " + key + " = " + headers.get(key));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-Type", "application/json; UTF-8");
                    header.put("Authorization", "key=" + fcmServerKey);
                    return header;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
