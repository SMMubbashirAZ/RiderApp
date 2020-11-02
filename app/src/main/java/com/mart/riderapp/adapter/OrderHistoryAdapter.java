package com.mart.riderapp.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mart.riderapp.Constants.AppConstants;
import com.mart.riderapp.Constants.URLS;
import com.mart.riderapp.R;
import com.mart.riderapp.activities.TrackActivity;
import com.mart.riderapp.listeners.ServerRequestListener;
import com.mart.riderapp.model.OrderHistoryModel;
import com.mart.riderapp.serverRequestHelper.MyServerRequest;
import com.mart.riderapp.sharedPref.SessionManager;
import com.mart.riderapp.utils.HexagonMaskView;
import com.mart.riderapp.utils.UtilityFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by WeMartDevelopers .
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private static final String TAG = OrderHistoryAdapter.class.getSimpleName();
    private List<OrderHistoryModel> ordersList;
    private Context context;
    private SessionManager sessionManager;

    public OrderHistoryAdapter(List<OrderHistoryModel> ordersList, Context context) {
        this.ordersList = ordersList;
        this.context = context;
        sessionManager=SessionManager.getInstance(context);
    }

    public void addItem(OrderHistoryModel myOrderModel) {
        ordersList.add(myOrderModel);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_recylerview,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderHistoryModel dataModel = ordersList.get(position);
        holder.order_shop_name.setText(dataModel.getOrder_shop_name());
        holder.order_delivered.setText(dataModel.getOrder_user_name());
        holder.order_orders.setText(dataModel.getOrder_orders());
        holder.order_price.setText(MessageFormat.format("Rs.{0}",dataModel.getOrder_total_price()));
        holder.status.setText(dataModel.getOrder_status());

        holder.tvuserNo.setText(dataModel.getOrder_user_no());
        holder.tv_user_loc.setText(dataModel.getOrder_user_location());
        holder.tv_shop_loc.setText(dataModel.getOrder_shop_location());
        Date date;
        String formattedDate = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dataModel.getOrder_date());
            // format the java.util.Date object to the desired format
            formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.order_date.setText(formattedDate);
        if (dataModel.isApproved()) {
            holder.tvNotApproved.setVisibility(View.GONE);
            holder.tvApproved.setVisibility(View.VISIBLE);
            holder.track.setVisibility(View.VISIBLE);

        } else {
            holder.tvApproved.setVisibility(View.GONE);
            holder.track.setVisibility(View.GONE);
            holder.tvNotApproved.setVisibility(View.VISIBLE);
        }


        holder.tvNotApproved.setOnClickListener(view -> {

            if (UtilityFunctions.isNetworkAvailable(context)){
                showApprovedOrderDialog(context,"Alert","Do you want to deliver this order",position,"approved");
            }else{
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        holder.track.setOnClickListener(view -> {
            showApprovedOrderDialog(context,"Alert","Do ou want to start this Order",position," ");
        });
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loader_bck)
                .error(R.drawable.ic_baseline_error_outline_24);

        Glide.with(context)
                .load(URLS.BASEIMGURL+dataModel.getOrder_shop_image())
                .apply(options)
                .thumbnail(Glide.with(context).load(R.drawable.loader_bck))
                .into(holder.order_shop_image);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public void showApprovedOrderDialog(Context context, String title, String message, int position,String tag) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_layout, new FrameLayout(context), false);
        final Dialog myDialog = new Dialog(context, R.style.FullDialogTheme);
        myDialog.setContentView(view);

        TextView titleTv = view.findViewById(R.id.custom_dialog_title);
        TextView messageTv = view.findViewById(R.id.custom_dialog_msg);
        Button okBtn = view.findViewById(R.id.custom_dialog_ok_btn);
        Button cancelBtn = view.findViewById(R.id.custom_dialog_cancel_btn);
        Button acceptBtn = view.findViewById(R.id.custom_dialog_accept_btn);
        cancelBtn.setVisibility(View.VISIBLE);
        acceptBtn.setVisibility(View.VISIBLE);
        okBtn.setVisibility(View.GONE);

        titleTv.setText(title);
        messageTv.setText(message);
//
        acceptBtn.setOnClickListener(v -> {

            if (tag.equals("approved")){
                MyServerRequest myServerRequest = new MyServerRequest(context, URLS.approvedOder + "/" + ordersList.get(position).getOrderId() + "/" + "6", new ServerRequestListener() {
                    @Override
                    public void onPreResponse() {
                        UtilityFunctions.showProgressDialog(context,true);
                    }

                    @Override
                    public void onPostResponse(JSONObject jsonResponse, int requestCode) {

                        UtilityFunctions.hideProgressDialog(true);
                        try {
                            if (jsonResponse.getBoolean(AppConstants.HAS_RESPONSE)) {
                                Toast.makeText(context, " " + jsonResponse.getString(AppConstants.RESPONSE), Toast.LENGTH_SHORT).show();
                                ordersList.get(position).setApproved(true);
                                notifyDataSetChanged();
//                        productModelList.remove(position);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position,productModelList.size());
                            }else{
                                Toast.makeText(context, " " + jsonResponse.getString(AppConstants.MESSAGE), Toast.LENGTH_SHORT).show();
                            }
                            Log.i(TAG, "onPostResponse: "+jsonResponse.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                myServerRequest.sendGetRequest();
            }else{

                sessionManager.set(AppConstants.ORDER_ID,ordersList.get(position).getOrderId());
                sessionManager.createOrderObject(ordersList.get(position));
                context.startActivity(new Intent(context,TrackActivity.class));
                ((Activity)context).finish();
            }
            myDialog.dismiss();
        });
        cancelBtn.setOnClickListener(v -> {
            myDialog.dismiss();
        });

        myDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        myDialog.getWindow().setDimAmount(0.90f);
        myDialog.getWindow().getDecorView().setBackground(new ColorDrawable(Color.TRANSPARENT));
        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();
    }

    public void filterList(List<OrderHistoryModel> filterdNames) {
        this.ordersList = filterdNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvApproved,tvuserNo, tv_user_loc, tv_shop_loc,tvNotApproved,order_shop_name,order_delivered,order_date,order_price,order_orders,status;
        private HexagonMaskView order_shop_image;
        private ImageView track;

        private ViewHolder(View view) {
            super(view);

            order_shop_name = view.findViewById(R.id.tv_order_history_shop_name);
            order_delivered = view.findViewById(R.id.tv_order_history_delivered);
            order_date = view.findViewById(R.id.tv_order_history_date);
            order_price = view.findViewById(R.id.tv_order_history_price);
            order_orders = view.findViewById(R.id.tv_order_history_order);
            track = view.findViewById(R.id.iv_order_history_track);
            tvuserNo = view.findViewById(R.id.tv_order_history_number);
            tv_shop_loc = view.findViewById(R.id.tv_order_history_shop_loc);
            tv_user_loc = view.findViewById(R.id.tv_order_history_user_loc);

               tvApproved = itemView.findViewById(R.id.tv_order_history_approved);
            tvNotApproved = itemView.findViewById(R.id.tv_order_history_not_approved);
            status = itemView.findViewById(R.id.tv_order_history_status);
            order_shop_image = view.findViewById(R.id.hmv_order_history_image);

        }
    }
}
