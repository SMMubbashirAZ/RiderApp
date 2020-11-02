package com.mart.riderapp.activities;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.Constants.URLS;
import com.mart.riderapp.R;
import com.mart.riderapp.formap.FetchURL;
import com.mart.riderapp.formap.TaskLoadedCallback;
import com.mart.riderapp.listeners.ServerRequestListener;
import com.mart.riderapp.model.OrderHistoryModel;
import com.mart.riderapp.serverRequestHelper.MyServerRequest;
import com.mart.riderapp.sharedPref.SessionManager;
import com.mart.riderapp.utils.MapsResponse;
import com.mart.riderapp.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

public class TrackActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback, LocationListener {
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;

    private boolean isDocOverlyOpen = false;
    private LinearLayout ll_openDialog, ll_orderlist;
    private SessionManager sessionManager = SessionManager.getInstance(this);
    private View view_overlay;
    private MapsResponse mapsResponse = new MapsResponse();
    private double lat = 0.0, lng = 0.0;
    private Handler mHandler = new Handler();
    private Runnable runnable;
    private Button pickOrder,deliveredOrder;

    private String status="";
    private ImageView iv_closelist, iv_cust_no;
    private OrderHistoryModel orderHistoryModel;
    private TextView tv_date, tv_price, tv_order, tv_shop_name, tv_shop_address, tv_cust_name, tv_cust_no, tv_cust_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        orderHistoryModel = sessionManager.getOrdersObj();
        BindId();
        if (orderHistoryModel == null) {
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } else {
            getCurentLocation();
            Date date;
            String formattedDate = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(orderHistoryModel.getOrder_date());
                // format the java.util.Date object to the desired format
                formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tv_date.setText(formattedDate);
            tv_order.setText(orderHistoryModel.getOrder_orders());
            tv_price.setText(MessageFormat.format("Rs.{0}/-", orderHistoryModel.getOrder_total_price()));
            tv_shop_name.setText(orderHistoryModel.getOrder_shop_name());
            tv_shop_address.setText(orderHistoryModel.getOrder_shop_location());
            tv_cust_name.setText(orderHistoryModel.getOrder_user_name());
            tv_cust_no.setText(orderHistoryModel.getOrder_user_no());
            tv_cust_address.setText(orderHistoryModel.getOrder_user_location());
            iv_cust_no.setOnClickListener(view -> {
                Uri u = Uri.parse("tel:" + orderHistoryModel.getOrder_user_no());
                Intent intent = new Intent(Intent.ACTION_DIAL, u);
                try {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(intent);
                } catch (SecurityException s) {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(TrackActivity.this, "" + s.toString(), Toast.LENGTH_LONG)
                            .show();
                }
            });
            tv_cust_no.setOnClickListener(view -> {
                Uri u = Uri.parse("tel:" + orderHistoryModel.getOrder_user_no());
                Intent intent = new Intent(Intent.ACTION_DIAL, u);
                try {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(intent);
                } catch (SecurityException s) {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(TrackActivity.this, "" + s.toString(), Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map_track_order);
        mapFragment.getMapAsync(this);

        ll_openDialog.setOnClickListener(view -> {
            slideUp(ll_orderlist, view_overlay);
            isDocOverlyOpen = true;
        });
        iv_closelist.setOnClickListener(view -> {
            slideDown(ll_orderlist, view_overlay);
            isDocOverlyOpen = false;

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDocOverlyOpen) {
            slideDown(ll_orderlist, view_overlay);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Log.d("mylog", "Added Markers");
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);
        getCurentLocation();
        setlocationUpdates(new LatLng(orderHistoryModel.getShop_lat(), orderHistoryModel.getShop_lng())
                , new LatLng(lat,lng),"Shop Location","Rider Location");

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        postLatLngApi(location.getLatitude(),location.getLongitude());

        if (status.equals("")){
            setlocationUpdates(new LatLng(orderHistoryModel.getShop_lat(), orderHistoryModel.getShop_lng())
                    , new LatLng(location.getLatitude(),location.getLongitude()),"Shop Location","Rider Location");
            status="on the way";
        }
        if (status.equals("on the way")){
            if (location.getLatitude()!=orderHistoryModel.getShop_lat()&&location.getLongitude()!=orderHistoryModel.getShop_lng()){
                setlocationUpdates(new LatLng(orderHistoryModel.getShop_lat(), orderHistoryModel.getShop_lng())
                        , new LatLng(location.getLatitude(),location.getLongitude()),"Shop Location","Rider Location");
            }else{
                status="pick order";
            }
        }
        if (status.equals("pick order")){
            if (location.getLatitude()!=orderHistoryModel.getUser_lat()&&location.getLongitude()!=orderHistoryModel.getUser_lng()){
                setlocationUpdates(new LatLng(orderHistoryModel.getUser_lat(), orderHistoryModel.getUser_lng())
                        , new LatLng(location.getLatitude(),location.getLongitude()),"User Location","Rider Location");
            }else{
                status="delivered";

            }
        }


//        while ()/
//        while(){
//            mHandler.postDelayed(runnable = new Runnable() {
//                public void run() {
//                    mHandler.postDelayed(runnable, 5000);
//                    Toast.makeText(TrackActivity.this, "location Update", Toast.LENGTH_SHORT).show();
//                }
//            }, 5000);
//        }

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        MarkerOptions pickUpOptions = new MarkerOptions()
//                .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                .draggable(false)
//                .title("Shop Location")
//                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_baseline_location_on_24));
//        Marker pickUpMarker = mMap.addMarker(pickUpOptions);
//        builder.include(pickUpMarker.getPosition());
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private void PickOrderApi(){
        MyServerRequest myServerRequest = new MyServerRequest(TrackActivity.this, URLS.PickOrder + "/" + orderHistoryModel.getOrderId(), new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(TrackActivity.this,true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {
                UtilityFunctions.hideProgressDialog(true);
                try {
                    if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)) {
                        Toast.makeText(TrackActivity.this, "" + jsonResponse.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(TrackActivity.this, ""+jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        myServerRequest.sendGetRequest();
    }
    private void DeliveredOrderApi(){
        MyServerRequest myServerRequest = new MyServerRequest(TrackActivity.this, URLS.PickOrder + "/" + orderHistoryModel.getOrderId(), new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(TrackActivity.this,true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {
                UtilityFunctions.hideProgressDialog(true);
                try {
                    if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)) {
                        Toast.makeText(TrackActivity.this, "" + jsonResponse.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(TrackActivity.this, ""+jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        myServerRequest.sendGetRequest();
    }

    private void BindId() {

        ll_openDialog = findViewById(R.id.ll_track_openDialog);
        ll_orderlist = findViewById(R.id.ll_track_code_box);
        view_overlay = findViewById(R.id.view_track__overlayloader);
        iv_closelist = findViewById(R.id.iv_track_closelist);
        iv_cust_no = findViewById(R.id.iv_track_cust_no);

        tv_date = findViewById(R.id.tv_track_date);
        tv_price = findViewById(R.id.tv_track_price);
        tv_order = findViewById(R.id.tv_track_cust_order);
        tv_shop_name = findViewById(R.id.tv_track_shop_name);
        tv_shop_address = findViewById(R.id.tv_track_shop_address);
        tv_cust_name = findViewById(R.id.tv_track_cust_name);
        tv_cust_address = findViewById(R.id.tv_track_cust_address);
        tv_cust_no = findViewById(R.id.tv_track_cust_no);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    private BitmapDescriptor bitmapDescriptorFromVector(Activity activity, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(activity, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void slideUp(View view, View overlay) {
        overlay.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        overlay.animate().alpha(1).setDuration(600).setListener(null).start();
        view.animate().translationY(900).alpha(1).setInterpolator(new DecelerateInterpolator(3f)).setListener(null).setDuration(600).start();

    }

    public void slideDown(final View view, final View overlay) {
        overlay.animate().alpha(0).setDuration(600).setListener(null).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                overlay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
        view.animate().translationY(view.getHeight()).alpha(0).setInterpolator(new DecelerateInterpolator(3f)).setDuration(600).setListener(null).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();

    }
    private void setlocationUpdates(LatLng shopLatLng, LatLng userLatLang,String FirstTitle,String SecondTitle) {
        mMap.clear();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions pickUpOptions = new MarkerOptions()
                .position(shopLatLng)
                .draggable(false)
                .title(FirstTitle)
                .icon(bitmapDescriptorFromVector(this, R.drawable.user_icon_managed));
        Marker pickUpMarker = mMap.addMarker(pickUpOptions);
        builder.include(pickUpMarker.getPosition());

        MarkerOptions dropOffOptions = new MarkerOptions()
                .position(userLatLang)
                .draggable(false)
                .title(SecondTitle)
                .icon(bitmapDescriptorFromVector(this, R.drawable.destination_icon_managed));
        Marker dropOffMarker = mMap.addMarker(dropOffOptions);
        builder.include(dropOffMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        Double temp = getResources().getDisplayMetrics().heightPixels / 3.0;
        int height = temp.intValue();
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

        new FetchURL(this).execute(getUrl(shopLatLng, userLatLang, "driving"), "driving");

    }


    private void getCurentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(TrackActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(TrackActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocIndex = locationResult.getLocations().size() - 1;
                            lat = locationResult.getLocations().get(latestLocIndex).getLatitude();
                            lng = locationResult.getLocations().get(latestLocIndex).getLongitude();
                        }
                    }
                }, Looper.getMainLooper());

        postLatLngApi(lat,lng);
    }

    private void postLatLngApi(double late, double lngi){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("OrderID", orderHistoryModel.getOrderId());
            jsonObject.put("Latitude", late);
            jsonObject.put("Longitude", lngi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyServerRequest myServerRequest = new MyServerRequest(TrackActivity.this, URLS.AddRiderLocation, jsonObject
                , new ServerRequestListener() {
            @Override
            public void onPreResponse() {
                UtilityFunctions.showProgressDialog(TrackActivity.this, true);
            }

            @Override
            public void onPostResponse(JSONObject jsonResponse, int requestCode) {
                UtilityFunctions.hideProgressDialog(true);
                try {
                    if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)) {
                        Toast.makeText(TrackActivity.this, "" + jsonResponse.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(TrackActivity.this, ""+jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        myServerRequest.sendRequest();

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

}