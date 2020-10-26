package com.mart.riderapp.model;

/**
 * Created by WeMartDevelopers .
 */
public class UserModel {
    private int user_id;
    private  String user_name,user_phn_no,user_email,user_password,user_image,user_address;
    private double user_lat,user_lng;

    public UserModel(String user_phn_no, String user_password) {
        this.user_phn_no = user_phn_no;
        this.user_password = user_password;
    }

    public UserModel(int user_id, String user_name, String user_phn_no, String user_email, String user_password) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_phn_no = user_phn_no;
        this.user_email = user_email;
        this.user_password = user_password;
    }

    public UserModel(String user_name, String user_phn_no, String user_email, String user_password, String user_image) {
        this.user_name = user_name;
        this.user_phn_no = user_phn_no;
        this.user_email = user_email;
        this.user_password = user_password;
    }

    public UserModel(String user_name, String user_phn_no, String user_email, String user_password, String user_image, String user_address, double user_lat, double user_lng) {

        this.user_name = user_name;
        this.user_phn_no = user_phn_no;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_image = user_image;
        this.user_address = user_address;
        this.user_lat = user_lat;
        this.user_lng = user_lng;
    }

    public UserModel() {
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phn_no() {
        return user_phn_no;
    }

    public void setUser_phn_no(String user_phn_no) {
        this.user_phn_no = user_phn_no;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
}
