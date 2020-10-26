package com.mart.riderapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.Constants.URLS;
import com.mart.riderapp.R;
import com.mart.riderapp.listeners.ServerRequestListener;
import com.mart.riderapp.serverRequestHelper.MyServerRequest;
import com.mart.riderapp.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.UUID;

public class ForgetPasswordActivity extends BaseCompatActivity {

    private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
    private LinearLayout ll_email, ll_code, ll_update_pass;
    private TextView tv_back;
    private Button btn_send_email,btn_verify_code,btn_update_pass;
    private TextInputEditText tiedt_email, tiedt_code, tiedt_new_pass, tiedt_conf_pass;

    private String randomCode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        BindingAndInitializingComponents();

        btn_send_email.setOnClickListener(view -> EmailSend());
        btn_verify_code.setOnClickListener(view -> VerifyCode());
        btn_update_pass.setOnClickListener(view -> UpdatePassword());
        tv_back.setOnClickListener(view -> onBackPressed());
    }

    private void UpdatePassword() {
        if (tiedt_new_pass.getText().toString().isEmpty()){
            tiedt_new_pass.setError("Please enter password to proceed");
            return;
        }
        if (tiedt_conf_pass.getText().toString().isEmpty()){
            tiedt_conf_pass.setError("Please enter password to proceed");
            return;
        }

        if (!tiedt_new_pass.getText().toString().equals(tiedt_new_pass.getText().toString())){
            tiedt_conf_pass.setError("Passwords do not match");
            return;
        }
        UpdatePasswordApi(tiedt_email.getText().toString().trim(),tiedt_new_pass.getText().toString().trim());

    }

    private void UpdatePasswordApi(String Email,String Pass) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email",Email);
            jsonObject.put("Password",Pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyServerRequest myServerRequest = new MyServerRequest(ForgetPasswordActivity.this, URLS.UpdatePass, jsonObject, new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(ForgetPasswordActivity.this,true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {

                UtilityFunctions.hideProgressDialog(true);
                try {
                    if (jsonObject.getBoolean(AppConstants.HAS_RESPONSE)){
                        Toast.makeText(ForgetPasswordActivity.this, "Password updated Sucessfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
                        finish();
                        finishAffinity();
                    }else{
                        Toast.makeText(ForgetPasswordActivity.this, " "+jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        myServerRequest.sendRequest();
    }

    private void VerifyCode() {
        if (tiedt_code.getText().toString().isEmpty()){
            tiedt_code.setError("Please enter your Code to proceed");
            return;
        }
        if (tiedt_code.getText().toString().trim().equals(randomCode)){
            ll_update_pass.setVisibility(View.VISIBLE);
            ll_code.setVisibility(View.GONE);
            btn_verify_code.setVisibility(View.GONE);
            btn_send_email.setVisibility(View.GONE);
            btn_update_pass.setVisibility(View.VISIBLE);
        } else{
            showDialogue(R.layout.custom_dialog_layout,"You have enter incorrect code","alert",null);
        }


    }

    private void EmailSend() {

        final String randomCode2 = UUID.randomUUID().toString().substring(0, 5);
        randomCode=randomCode2;
        Log.i(TAG, "EmailSend: "+randomCode);

        if (tiedt_email.getText().toString().isEmpty()){
            tiedt_email.setError("Please enter your Email to proceed");
            return;
        }

        EmailVerified(tiedt_email.getText().toString().trim(),randomCode);



    }
    public void EmailVerified(String Email, String Code) {

        String url = MessageFormat.format("{0}/{1}/{2}", URLS.forgetPassEmail, Email, Code);
        MyServerRequest myServerRequest = new MyServerRequest(this, url, new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(ForgetPasswordActivity.this, true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {
                UtilityFunctions.hideProgressDialog(true);
                if (jsonResponse != null) {
                    try {
                        if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)){
                            Toast.makeText(ForgetPasswordActivity.this, " "+jsonResponse.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                            ll_email.setVisibility(View.GONE);
                            ll_code.setVisibility(View.VISIBLE);
                            btn_verify_code.setVisibility(View.VISIBLE);
                            btn_send_email.setVisibility(View.GONE);
                            btn_update_pass.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(ForgetPasswordActivity.this, " "+jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "onResponse: " + jsonResponse.toString());


                }
            }
        });
        myServerRequest.sendGetRequest();


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void BindingAndInitializingComponents() {
        ll_email = findViewById(R.id.ll_forget_email);
        ll_code = findViewById(R.id.ll_forget_code);
        ll_update_pass = findViewById(R.id.ll_forget_newpass);
        tv_back = findViewById(R.id.tv_forget_back);
        btn_send_email = findViewById(R.id.btn_forget_send_email);
        btn_verify_code = findViewById(R.id.btn_forget_enter_code);
        btn_update_pass = findViewById(R.id.btn_forget_update_pass);
        tiedt_email = findViewById(R.id.tiedt_forget_email);
        tiedt_code = findViewById(R.id.tiedt_forget_code);
        tiedt_new_pass = findViewById(R.id.tiedt_forget_pass);
        tiedt_conf_pass = findViewById(R.id.tiedt_forget_conf_pass);
    }
}