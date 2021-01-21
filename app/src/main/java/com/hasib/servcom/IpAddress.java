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

@Entity(tableName = "ipaddresses")
public class IpAddress {
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "ip")
    String ip;

    @Ignore
    public IpAddress(String ip) {
        this.ip = ip;
    }

    public IpAddress(int id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
