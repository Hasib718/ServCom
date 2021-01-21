package com.hasib.servcom;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:29 PM
 */

@Entity(tableName = "callDetails")
public class CallDetails {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;
    @ColumnInfo(name = "callType")
    private String callType;
    @ColumnInfo(name = "callDate")
    private String callDate;
    @ColumnInfo(name = "callDuration")
    private String callDuration;

    @Ignore
    public CallDetails(String phoneNumber, String callType, String callDate, String callDuration) {
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    public CallDetails(Integer id, String phoneNumber, String callType, String callDate, String callDuration) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.callDuration = callDuration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    @Override
    public String toString() {
        return "CallDetails{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", callType='" + callType + '\'' +
                ", callDate='" + callDate + '\'' +
                ", callDuration='" + callDuration + '\'' +
                '}';
    }
}
