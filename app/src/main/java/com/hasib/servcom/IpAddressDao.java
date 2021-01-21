package com.hasib.servcom;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:29 PM
 */

@Dao
public interface IpAddressDao {

    @Query("SELECT * FROM IPADDRESSES ORDER BY ID")
    List<IpAddress> loadAllIpAddresses();

    @Insert
    void insertIpAddress(IpAddress ipAddress);

    @Update
    void updateIpAddress(IpAddress ipAddress);

    @Delete
    void delete(IpAddress ipAddress);

    @Query("SELECT * FROM IPADDRESSES WHERE id = :id")
    IpAddress loadIpAddressById(int id);
}
