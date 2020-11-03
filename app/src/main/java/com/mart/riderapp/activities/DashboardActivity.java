package com.mart.riderapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.Constants.URLS;
import com.mart.riderapp.R;
import com.mart.riderapp.adapter.OrderHistoryAdapter;
import com.mart.riderapp.listeners.ServerRequestListener;
import com.mart.riderapp.model.OrderHistoryModel;
import com.mart.riderapp.model.UserModel;
import com.mart.riderapp.serverRequestHelper.MyServerRequest;
import com.mart.riderapp.sharedPref.SessionManager;
import com.mart.riderapp.utils.UtilityFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardActivity extends BaseCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    @BindView(R.id.rv_home_prod)
    RecyclerView rv_order;
    @BindView(R.id.edt_home_search)
    EditText edt_search;
    @BindView(R.id.tv_no_active_order_history)
    TextView tv_no_active; private View include_layout;
    private ImageView iv_logout;
    private List<OrderHistoryModel> orderHistoryModelList = new ArrayList<>();
    private SessionManager sessionManager=SessionManager.getInstance(this);
    private OrderHistoryAdapter orderAdapter;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        include_layout = findViewById(R.id.include_home);
        iv_logout = include_layout.findViewById(R.id.iv_appbar_logout_icon);
        iv_logout.setOnClickListener(view -> {
            showDialogue(R.layout.dialogue_logout_layout,"You will be logged out of the application. \\nDo You wish to continue","logout",null);

        });
        if (UtilityFunctions.isNetworkAvailable(this)){
            userModel=sessionManager.getUserSession();
            if (userModel==null){
                getAllProducts(String.valueOf(6));
                tv_no_active.setVisibility(View.GONE);
                edt_search.setVisibility(View.VISIBLE);
            }else{
                edt_search.setVisibility(View.VISIBLE);
                tv_no_active.setVisibility(View.GONE);
                getAllProducts(String.valueOf(sessionManager.getUserSession().getUser_id()));

            }

        }else {
            UtilityFunctions.hideProgressDialog(true);
            edt_search.setVisibility(View.GONE);
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString(), orderHistoryModelList);
            }
        });
    }

    private void getAllProducts(String userId) {

        String url = URLS.getOrderByUserID+"/"+userId;
        MyServerRequest myServerRequest = new MyServerRequest(DashboardActivity.this, url, new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(DashboardActivity.this,true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {

                UtilityFunctions.hideProgressDialog(true);
                try {
                    if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)) {
                        tv_no_active.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            OrderHistoryModel orderHistoryModel = new OrderHistoryModel();
                            orderHistoryModel.setOrderId(jsonArray.getJSONObject(i).getString("orderID"));
                            orderHistoryModel.setOrder_user_name(jsonArray.getJSONObject(i).getString("userName"));
                            orderHistoryModel.setOrder_user_no(jsonArray.getJSONObject(i).getString("userNumber"));
                            orderHistoryModel.setOrder_shop_name(jsonArray.getJSONObject(i).getString("shopName"));
                            orderHistoryModel.setOrder_shop_image(jsonArray.getJSONObject(i).getString("shopImg"));
                            orderHistoryModel.setShop_lat(jsonArray.getJSONObject(i).getDouble("shopLat"));
                            orderHistoryModel.setShop_lng(jsonArray.getJSONObject(i).getDouble("shopLang"));
                            orderHistoryModel.setOrder_shop_location(jsonArray.getJSONObject(i).getString("shopAddress"));
                            orderHistoryModel.setOrder_status(jsonArray.getJSONObject(i).getString("statusName"));
                            orderHistoryModel.setOrder_orders(jsonArray.getJSONObject(i).getString("productDetails"));
                            orderHistoryModel.setOrder_total_price(jsonArray.getJSONObject(i).getString("totalPrice"));
                            orderHistoryModel.setUser_lat(jsonArray.getJSONObject(i).getDouble("userLat"));
                            orderHistoryModel.setUser_lng(jsonArray.getJSONObject(i).getDouble("userLang"));
                            orderHistoryModel.setOrder_user_location(jsonArray.getJSONObject(i).getString("userLocation"));
                            orderHistoryModel.setOrder_date(jsonArray.getJSONObject(i).getString("orderDate"));
                            orderHistoryModel.setStatusId(jsonArray.getJSONObject(i).getInt("statusID"));
                            if (jsonArray.getJSONObject(i).getInt("statusID")==3){
                                orderHistoryModel.setApproved(true);
                            }else if (jsonArray.getJSONObject(i).getInt("statusID")==5){
                                orderHistoryModel.setApproved(true);
                            }else if (jsonArray.getJSONObject(i).getInt("statusID")==4){
                                orderHistoryModel.setApproved(true);
                            }else{
                                orderHistoryModel.setApproved(false);
                            }
                            orderHistoryModelList.add(orderHistoryModel);
                        }
                        orderAdapter = new OrderHistoryAdapter( orderHistoryModelList,DashboardActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DashboardActivity.this);
                        rv_order.setLayoutManager(mLayoutManager);
                        rv_order.setItemAnimator(new DefaultItemAnimator());
                        rv_order.addItemDecoration(
                                new DividerItemDecoration(DashboardActivity.this, DividerItemDecoration.VERTICAL));
                        // Set adapter to recyclerView
                        rv_order.setAdapter(orderAdapter);
                    } else {

                        tv_no_active.setVisibility(View.VISIBLE);
                        Toast.makeText(DashboardActivity.this, " " + jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        myServerRequest.sendGetRequest();
    }
    private void filter(String text, List<OrderHistoryModel> filtr_list) {
        //new array list that will hold the filtered data
        List<OrderHistoryModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (OrderHistoryModel s : filtr_list) {
            //if the existing elements contains the search input
            if (s.getOrder_shop_name().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        if (filterdNames.size() == 0) {
            Toast.makeText(DashboardActivity.this, "No Record Found", Toast.LENGTH_SHORT).show();
        }
        //calling a method of the adapter class and passing the filtered list
        orderAdapter.filterList(filterdNames);
    }
}