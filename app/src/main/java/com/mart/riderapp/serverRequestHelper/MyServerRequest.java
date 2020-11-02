package com.mart.riderapp.serverRequestHelper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.R;
import com.mart.riderapp.listeners.CustomInfoDialogListener;
import com.mart.riderapp.listeners.ServerRequestListener;
import com.mart.riderapp.model.UserModel;
import com.mart.riderapp.sharedPref.SessionManager;
import com.mart.riderapp.utils.HeaderDialog;
import com.mart.riderapp.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;


public class MyServerRequest {

    private String url;
    private JSONObject jsonObject;
    private int requestCode = 0;
    private Context context;
    private RequestQueue queue;
    private CustomInfoDialogListener listener;
    private SessionManager userSettings;
    private ServerRequestListener serverRequestListener;

    public MyServerRequest(Context context, String url, JSONObject jsonObject, int requestCode, ServerRequestListener serverRequestListener) {
        this.url = url;
        this.jsonObject = jsonObject;
        this.requestCode = requestCode;
        this.context = context;
        this.serverRequestListener = serverRequestListener;
        queue = VolleySingleton.getInstance(context).getReq_queue();
        userSettings = SessionManager.getInstance(context);
    }

    public MyServerRequest(Context context, String url, JSONObject jsonObject, ServerRequestListener serverRequestListener) {
        this.url = url;
        this.jsonObject = jsonObject;
        this.context = context;
        this.serverRequestListener = serverRequestListener;
        queue = VolleySingleton.getInstance(context).getReq_queue();
        userSettings = SessionManager.getInstance(context);
    }

    public MyServerRequest(Context context, String url, int requestCode, ServerRequestListener serverRequestListener) {
        this.url = url;
        this.context = context;
        this.requestCode = requestCode;
        this.serverRequestListener = serverRequestListener;
        queue = VolleySingleton.getInstance(context).getReq_queue();
        userSettings = SessionManager.getInstance(context);
    }

    public MyServerRequest(Context context, String url, ServerRequestListener serverRequestListener) {
        this.url = url;
        this.context = context;
        this.serverRequestListener = serverRequestListener;
        queue = VolleySingleton.getInstance(context).getReq_queue();
        userSettings = SessionManager.getInstance(context);
    }


    public void sendRequest() {

        if (UtilityFunctions.isNetworkAvailable(context)) {
            serverRequestListener.onPreResponse();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try {
                    if (response.getBoolean(AppConstants.HAS_RESPONSE)) {
                        UtilityFunctions.hideProgressDialog(true);
                        serverRequestListener.onPostResponse(response, requestCode);
//                        context.startActivity(new Intent(context,LoginActivity.class));


                    } else {
                        UtilityFunctions.hideProgressDialog(true);
                        Toast.makeText(context, " "+response.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    UtilityFunctions.hideProgressDialog(true);

                }

            }, error -> {
                UtilityFunctions.hideProgressDialog(true);
                VolleyLog.e("TransactionRequest", "Error: " + error);
                Log.e("TransactionRequest", "Error: " + error);
                VolleyErrorHelper.ShowError(error, context, listener);
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjectRequest);
        } else {
            UtilityFunctions.hideProgressDialog(true);
            HeaderDialog.showInfoDialog(context, context.getResources().getString(R.string.messages),
                    "No internet connection found please check your wifi or network state", () -> ((Activity) context).finish());
        }

    }

    public void sendGetRequest() {

        if (UtilityFunctions.isNetworkAvailable(context)) {
            serverRequestListener.onPreResponse();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
                serverRequestListener.onPostResponse(response, requestCode);

            }, error -> {
                UtilityFunctions.hideProgressDialog(true);
                VolleyLog.e("TransactionRequest", "Error: " + error);
                Log.e("TransactionRequest", "Error: " + error);
                VolleyErrorHelper.ShowError(error, context, listener);
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjectRequest);
        } else {
            HeaderDialog.showInfoDialog(context, context.getResources().getString(R.string.messages),
                    "No internet connection found please check your wifi or network state", () -> ((Activity) context).finish());
        }

    }

    public void sendLoginRequest() {

        if (UtilityFunctions.isNetworkAvailable(context)) {
            serverRequestListener.onPreResponse();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, response -> {
                try {
                    if (response.getBoolean(AppConstants.HAS_RESPONSE)) {
                        serverRequestListener.onPostResponse(response, requestCode);
                        userSettings.set(AppConstants.IS_USER_LOGIN,true);
                        UserModel userModel = new UserModel();
                        userModel.setUser_id(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getInt("id"));
                        userModel.setUser_name(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getString("name"));
                        userModel.setUser_phn_no(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getString("number"));
                        userModel.setUser_email(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getString("email"));
                        userModel.setUser_image(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getString("img"));
                        userModel.setUser_address(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getString("address"));
                        userModel.setUser_lat(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getDouble("latitude"));
                        userModel.setUser_lng(response.getJSONArray(AppConstants.RESPONSE).getJSONObject(0).getDouble("longitude"));
                        userSettings.createUserSession(userModel);
                    } else {
                        UtilityFunctions.hideProgressDialog(true);
                        Toast.makeText(context, response.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {

                }

            }, error -> {

                VolleyLog.e("TransactionRequest", "Error: " + error);
                Log.e("TransactionRequest", "Error: " + error);
                VolleyErrorHelper.ShowError(error, context, listener);
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonObjectRequest);
        } else {
            HeaderDialog.showInfoDialog(context, context.getResources().getString(R.string.messages),
                    "No internet connection found please check your wifi or network state", () -> ((Activity) context).finish());
        }



    }


    public void setCustomDialogListener(CustomInfoDialogListener listener) {
        this.listener = listener;
    }
}
