package com.hasib.servcom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public class IpAddressesAdapter extends RecyclerView.Adapter<IpAddressesAdapter.IpAddressHolder> {
    private final Context context;
    private List<IpAddress> ipAddressList;

    public IpAddressesAdapter(Context context) {
        this.context = context;
    }

    public IpAddressesAdapter(Context context, List<IpAddress> ipAddressList) {
        this.context = context;
        this.ipAddressList = ipAddressList;
    }

    @NonNull
    @Override
    public IpAddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IpAddressHolder(LayoutInflater.from(context).inflate(R.layout.layout_ips, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IpAddressHolder holder, int position) {
        holder.ipAddress.setText(ipAddressList.get(position).getIp());

        holder.ipAddress.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Utility.CURRENT_IP, ipAddressList.get(position).getIp());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (ipAddressList == null) {
            return 0;
        }
        return ipAddressList.size();
    }

    public List<IpAddress> getIpAddresses() {
        return ipAddressList;
    }

    public void setIpAddresses(List<IpAddress> ipAddressList) {
        this.ipAddressList = ipAddressList;
        notifyDataSetChanged();
    }

    class IpAddressHolder extends RecyclerView.ViewHolder {
        MaterialTextView ipAddress;

        public IpAddressHolder(@NonNull View itemView) {
            super(itemView);
            ipAddress = itemView.findViewById(R.id.ip);
        }
    }
}
