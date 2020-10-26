package com.mart.riderapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.Constants.URLS;
import com.mart.riderapp.R;
import com.mart.riderapp.utils.FileUtils;
import com.mart.riderapp.utils.UtilityFunctions;
import com.mobsandgeeks.saripaar.annotation.Order;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisteredActivity extends BaseCompatActivity {
    private static final String TAG = RegisteredActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    @BindView(R.id.tiedt_signup_name)
    TextInputEditText name;
    String imagePath = "";
    @BindView(R.id.tiedt_signup_phn_no)
    TextInputEditText phn_no;

    @BindView(R.id.civ_signup_image)
    CircleImageView profile_img;
    @BindView(R.id.tiedt_signup_email)
    @Order(3)
    TextInputEditText email;
    @BindView(R.id.tiedt_signup_pass)
    TextInputEditText password;
    @BindView(R.id.tiedt_signup_conf_pass)
    TextInputEditText conf_password;
    private int PICK_IMAGE_REQUEST = 1;
    private FileUtils fileUtils = new FileUtils();

    private String lastChar = "";
    private String user_name;
    private String user_number;
    private String user_email;
    private String user_password;
    private boolean isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);
        hideUI();
        isStoragePermissionGranted();
        phn_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int digits = phn_no.getText().toString().length();
                if (digits > 1)
                    lastChar = phn_no.getText().toString().substring(digits - 1);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int digits = phn_no.getText().toString().length();
                Log.d("LENGTH", "" + digits);
                if (!lastChar.equals("-")) {
                    if (digits == 4) {
                        phn_no.append("-");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

//                uploadFile("http://wemarz.somee.com/api/Registration/ShopRegister",new File(filePath));

                imagePath = fileUtils.getPath(this, uri);
                Log.e(TAG, "onActivityResult:   " + imagePath);
                Log.e(TAG, "onActivityResult: " + getExternalCacheDir().getAbsolutePath());
                Log.e(TAG, "onActivityBIT:   " + bitmap);

                profile_img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    @OnClick(R.id.iv_signup_select_image)
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.tv_signup_login)
    public void Login() {
        startActivity(new Intent(RegisteredActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            chooseImage();
        }


    }

    @OnClick(R.id.btn_signup_signup)
    public void Resgistered() {
        if (!RegisteredValidations()) {
            return;
        }

        register();
    }

    private void register() {
        UtilityFunctions.showProgressDialog(RegisteredActivity.this,true);
        File file = new File(imagePath);

        OkHttpClient client = new OkHttpClient();
        try {
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("jpg/png"), file))
                    .addFormDataPart(user_name, "shopName")
                    .addFormDataPart(user_email, "shopEmail")
                    .addFormDataPart(user_number, "shopNo")
                    .addFormDataPart(user_password, "shopPass")
                    .build();

            Log.i(TAG, "register: "+requestBody.toString());

            Request request = new Request.Builder()
                    .url(URLS.riderregistered)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(final Call call, final IOException e) {
                    // Handle the error

                    UtilityFunctions.hideProgressDialog(true);
                    Log.e(TAG, "onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(final Call call, Response response) throws IOException {


                    UtilityFunctions.hideProgressDialog(true);
                    if (response.code() == 200) {
                        try {
                            JSONObject data = new JSONObject(response.body().string());
                            if (data.getBoolean(AppConstants.HAS_RESPONSE)){
                                Log.e(TAG, "onResponse: " + response.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Log.i(TAG, "run: "+data.getString(AppConstants.RESPONSE));
                                            Toast.makeText(RegisteredActivity.this, data.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        startActivity(new Intent(RegisteredActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Log.i(TAG, "onResponse: "+data.getString(AppConstants.MESSAGE));
                                            Toast.makeText(RegisteredActivity.this, ""+data.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisteredActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            // Handle the error

            Log.e(TAG, "uploadFile: " + ex.getMessage());
        }
    }

    private boolean RegisteredValidations() {
        user_name = name.getText().toString().trim();
        user_number = phn_no.getText().toString().trim();
        user_email = email.getText().toString().trim();
        user_password = password.getText().toString().trim();


        if (user_name.matches("")) {
            name.setError("Required Field");
            return false;
        }
        if (user_number.matches("")) {
            phn_no.setError("Required Field");
            return false;
        }
        if (user_email.matches("")) {
            email.setError("Required Field");
            return false;
        }
        if (user_password.matches("")) {
            password.setError("Required Field");
            return false;
        }
//        if (addr.matches("")) {
//            shop_address.setError("Required Field");
//            return false;
//        }
//        if (imagePath.equals("")) {
//            Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (!user_password.equals(conf_password.getText().toString())) {
            conf_password.setError("Password mismatch");
            return false;
        }
        return true;
    }

    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
//                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
//            return true;/
        }
    }
}