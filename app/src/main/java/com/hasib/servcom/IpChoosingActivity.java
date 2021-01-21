package com.hasib.servcom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public class IpChoosingActivity extends AppCompatActivity {

    private static final String TAG = IpChoosingActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private IpAddressesAdapter ipAddressesAdapter;
    private AppDatabase db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_choosing);

        initiateViews();

        db = AppDatabase.getInstance(getApplicationContext());
        sharedPreferences = this.getSharedPreferences(Utility.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    private void initiateViews() {
        recyclerView = findViewById(R.id.recyclerViewTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ipAddressesAdapter = new IpAddressesAdapter(this);
        recyclerView.setAdapter(ipAddressesAdapter);


        findViewById(R.id.connection).setOnClickListener(v -> {
            final String ip = ((TextInputEditText) findViewById(R.id.ipAddressInput)).getText().toString().trim();
            Log.d(TAG, "initiateViews: " + ip);

            sharedPreferences.edit().putString(Utility.CURRENT_IP, ip).apply();
            AppExecutors.getInstance().diskIO().execute(() -> {
                db.ipAddressDao().insertIpAddress(new IpAddress(ip));
            });

            finish();

            Intent intent = new Intent(IpChoosingActivity.this, MainActivity.class);
            intent.putExtra(Utility.CURRENT_IP, ip);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    List<IpAddress> ipAddresses = ipAddressesAdapter.getIpAddresses();
                    db.ipAddressDao().delete(ipAddresses.get(viewHolder.getAdapterPosition()));
                    retrieveData();
                });
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveData();
    }

    private void retrieveData() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            final List<IpAddress> ipAddresses = db.ipAddressDao().loadAllIpAddresses();

            runOnUiThread(() -> {
                ipAddressesAdapter.setIpAddresses(ipAddresses);
            });
        });
    }
}