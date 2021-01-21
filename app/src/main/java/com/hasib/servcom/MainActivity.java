package com.hasib.servcom;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public class MainActivity extends AppCompatActivity implements MessageInterface {

    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private static final int REQUEST_CODE = 1001;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String IP_ADDRESS = "192.168.0.7:3000";
    private SharedPreferences sharedPreferences;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private RecyclerView recyclerView;

    private boolean clicked = false;

    private RetrofitInterface retrofitInterface;

    private ContentObserver contentObserver;

    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentObserver();

        checkPermissions();

        sharedPreferences = this.getSharedPreferences(Utility.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);


        Toast.makeText(this, "Connected to -- " + IP_ADDRESS, Toast.LENGTH_SHORT).show();
        retrofitInterface = RetrofitClient.getRetrofitClient(IP_ADDRESS).create(RetrofitInterface.class);


        initiateViews();
    }

    private void setContentObserver() {
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return true;
            }

            @Override
            public void onChange(boolean selfChange) {
                getLastCallRecord(MainActivity.this);
            }
        };

        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, contentObserver);
    }

    private void initiateViews() {
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        recyclerView = findViewById(R.id.mainRecyclerView);

        findViewById(R.id.addBtn).setOnClickListener(v -> {
            onAddButtonClicked();
        });

        findViewById(R.id.callBtn).setOnClickListener(v -> {
            makePhoneCall();
        });
        findViewById(R.id.messageBtn).setOnClickListener(v -> {
            sendSMS();
        });
        findViewById(R.id.emailBtn).setOnClickListener(v -> {
            sendEmail();
        });

    }

    private void sendEmail() {
        final View view1 = getLayoutInflater().inflate(R.layout.layout_email, null);

        createDialogBox(view1, "Email Dialog", "Send", (dialog, which) -> {
            Log.d(TAG, "onClick: email " + ((TextInputEditText) view1.findViewById(R.id.receiverEmail)).getText().toString());
            Log.d(TAG, "onClick: email " + ((TextInputEditText) view1.findViewById(R.id.subject)).getText().toString());
            Log.d(TAG, "onClick: email " + ((TextInputEditText) view1.findViewById(R.id.body)).getText().toString());

            Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
            selectorIntent.setData(Uri.parse("mailto:"));

            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{((TextInputEditText) view1.findViewById(R.id.receiverEmail)).getText().toString()});
            intent.putExtra(Intent.EXTRA_SUBJECT, ((TextInputEditText) view1.findViewById(R.id.subject)).getText().toString());
            intent.putExtra(Intent.EXTRA_TEXT, ((TextInputEditText) view1.findViewById(R.id.body)).getText().toString());
            intent.setSelector(selectorIntent);

            try {
                startActivity(Intent.createChooser(intent, getString(R.string.share_email_title)));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSMS() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

        this.registerReceiver(new SentMessageBroadcast(this), new IntentFilter(SENT));

        this.registerReceiver(
                new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(context, "Message Delivered Successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onReceive: Message Delivered Successfully");
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(context, "Message Delivered Failed", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onReceive: Message Delivered Failed");
                                break;
                        }
                    }
                }, new IntentFilter(DELIVERED));

        final View view2 = getLayoutInflater().inflate(R.layout.layout_sms, null);

        createDialogBox(view2, "SMS Dialog", "Send", (dialog, which) -> {
            try {
                SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(SubscriptionManager.getDefaultSmsSubscriptionId());
                smsManager.sendTextMessage(((TextInputEditText) view2.findViewById(R.id.cellNumber)).getText().toString().trim(),
                        null,
                        ((TextInputEditText) view2.findViewById(R.id.sms)).getText().toString(),
                        sentPI, deliveredPI);

                message = new Message(((TextInputEditText) view2.findViewById(R.id.cellNumber)).getText().toString().trim(), ((TextInputEditText) view2.findViewById(R.id.sms)).getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "SMS sent failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClickable(clicked);
        clicked = !clicked;
    }

    private void setClickable(boolean clicked) {
        if (!clicked) {
            findViewById(R.id.callBtn).setClickable(true);
            findViewById(R.id.messageBtn).setClickable(true);
            findViewById(R.id.emailBtn).setClickable(true);
        } else {
            findViewById(R.id.callBtn).setClickable(false);
            findViewById(R.id.messageBtn).setClickable(false);
            findViewById(R.id.emailBtn).setClickable(false);
        }
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            findViewById(R.id.callBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.messageBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.emailBtn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.callBtn).setVisibility(View.INVISIBLE);
            findViewById(R.id.messageBtn).setVisibility(View.INVISIBLE);
            findViewById(R.id.emailBtn).setVisibility(View.INVISIBLE);
        }
    }

    private void setVisibility(boolean clicked) {
        if (!clicked) {
            findViewById(R.id.callBtn).startAnimation(fromBottom);
            findViewById(R.id.messageBtn).startAnimation(fromBottom);
            findViewById(R.id.emailBtn).startAnimation(fromBottom);
            findViewById(R.id.addBtn).startAnimation(rotateOpen);
        } else {
            findViewById(R.id.callBtn).startAnimation(toBottom);
            findViewById(R.id.messageBtn).startAnimation(toBottom);
            findViewById(R.id.emailBtn).startAnimation(toBottom);
            findViewById(R.id.addBtn).startAnimation(rotateClose);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
        }
    }

    private void createDialogBox(View view, String title, String work, DialogInterface.OnClickListener onClickListener) {
        onAddButtonClicked();

        new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(title)
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton(work, onClickListener)
                .create()
                .show();
    }

    private void makePhoneCall() {
        final View view = getLayoutInflater().inflate(R.layout.layout_call, null);

        createDialogBox(view, "Calling Dialog", "Call", (dialog, which) -> {
            Log.d(TAG, "onClick: " + ((EditText) view.findViewById(R.id.phoneNumber)).getText().toString());

            final String number = ((EditText) view.findViewById(R.id.phoneNumber)).getText().toString();

            if (number.trim().length() > 0) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions();
                } else {
                    String dial = "tel:" + number;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    private void getLastCallRecord(Context ctx) {
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        cursor.moveToLast();
        String callType = cursor.getString(type);
        String callDate = cursor.getString(date);

        Date callDayTime = new Date(Long.valueOf(callDate));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dateString = formatter.format(callDayTime);

        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;

            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;

            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
        }

        CallDetails callDetails = new CallDetails(cursor.getString(number),
                dir,
                dateString,
                cursor.getString(duration));

        Log.d(TAG, "onOutgoingCallEnded: " + callDetails.toString());

        retrofitInterface.sendCallDetails(callDetails).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(ctx, response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMessageEvent(boolean event) {
        Log.d(TAG, "onMessageEvent: " + event);
        Log.d(TAG, "onMessageEvent: " + message.toString());

        if (event) {
            retrofitInterface.sendMessage(message).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Issue In message", Toast.LENGTH_SHORT).show();
        }
    }
}