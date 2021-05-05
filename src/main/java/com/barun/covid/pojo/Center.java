package com.barun.covid.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Center{
    private int center_id;
    private String name;
    private String address;
    private String state_name;
    private String district_name;
    private String block_name;
    private int pincode;
    private int lat;
    private int longitude;
    private String from;
    private String to;
    private String fee_type;
    private List<Session> sessions;

    public int getCenter_id() {
        return center_id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getState_name() {
        return state_name;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public String getBlock_name() {
        return block_name;
    }

    public int getPincode() {
        return pincode;
    }

    public int getLat() {
        return lat;
    }

    @JsonProperty("long")
    public int getLongitude() {
        return longitude;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getFee_type() {
        return fee_type;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setCenter_id(int center_id) {
        this.center_id = center_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public void setDistrict_name(String district_name) {
        this.district_name = district_name;
    }

    public void setBlock_name(String block_name) {
        this.block_name = block_name;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    @JsonProperty("long")
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @Override public String toString() {
        return "Center{" +
                "center_id=" + center_id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", state_name='" + state_name + '\'' +
                ", district_name='" + district_name + '\'' +
                ", block_name='" + block_name + '\'' +
                ", pincode=" + pincode +
                ", lat=" + lat +
                ", longitude=" + longitude +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", fee_type='" + fee_type + '\'' +
                ", sessions=" + sessions +
                '}';
    }
}
