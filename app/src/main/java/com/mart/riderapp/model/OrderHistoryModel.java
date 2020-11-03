package com.mart.riderapp.model;

/**
 * Created by WeMartDevelopers .
 */
public class OrderHistoryModel {
    private String orderId,order_shop_name,order_user_name,order_user_no,order_total_price,order_orders,order_date,order_shop_image,order_status,order_user_location,order_shop_location;
    private double shop_lat,shop_lng,user_lat,user_lng;
    private int statusId;


    private boolean isApproved;

    public OrderHistoryModel() {
    }

    public OrderHistoryModel(String order_shop_name, String order_total_price, String order_orders, String order_date, String order_shop_image) {
        this.order_shop_name = order_shop_name;
        this.order_total_price = order_total_price;
        this.order_orders = order_orders;
        this.order_date = order_date;
        this.order_shop_image = order_shop_image;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getOrder_shop_location() {
        return order_shop_location;
    }

    public void setOrder_shop_location(String order_shop_location) {
        this.order_shop_location = order_shop_location;
    }

    public String getOrder_user_no() {
        return order_user_no;
    }

    public void setOrder_user_no(String order_user_no) {
        this.order_user_no = order_user_no;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrder_user_name() {
        return order_user_name;
    }

    public void setOrder_user_name(String order_user_name) {
        this.order_user_name = order_user_name;
    }

    public String getOrder_user_location() {
        return order_user_location;
    }

    public void setOrder_user_location(String order_user_location) {
        this.order_user_location = order_user_location;
    }

    public double getShop_lat() {
        return shop_lat;
    }

    public void setShop_lat(double shop_lat) {
        this.shop_lat = shop_lat;
    }

    public double getShop_lng() {
        return shop_lng;
    }

    public void setShop_lng(double shop_lng) {
        this.shop_lng = shop_lng;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(double user_lat) {
        this.user_lat = user_lat;
    }

    public double getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(double user_lng) {
        this.user_lng = user_lng;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_shop_name() {
        return order_shop_name;
    }

    public void setOrder_shop_name(String order_shop_name) {
        this.order_shop_name = order_shop_name;
    }

    public String getOrder_total_price() {
        return order_total_price;
    }

    public void setOrder_total_price(String order_total_price) {
        this.order_total_price = order_total_price;
    }

    public String getOrder_orders() {
        return order_orders;
    }

    public void setOrder_orders(String order_orders) {
        this.order_orders = order_orders;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_shop_image() {
        return order_shop_image;
    }

    public void setOrder_shop_image(String order_shop_image) {
        this.order_shop_image = order_shop_image;
    }
}
