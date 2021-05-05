package com.barun.covid.pojo;

import java.util.Objects;

public class Availability {
    private String centerName;
    private String date;

    public String getCenterName() {
        return centerName;
    }

    public String getDate() {
        return date;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Availability(String centerName, String date) {
        this.centerName = centerName;
        this.date = date;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Availability that = (Availability) o;
        return centerName.equals(that.centerName) && date.equals(that.date);
    }

    @Override public int hashCode() {
        return Objects.hash(centerName, date);
    }
}
