package com.hasib.servcom;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public class SentMessageBroadcast extends BroadcastReceiver {

    private static final String TAG = SentMessageBroadcast.class.getSimpleName();

    private final MessageInterface messageInterface;

    public SentMessageBroadcast(MessageInterface messageInterface) {
        this.messageInterface = messageInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: Message Sent Successfully");
                messageInterface.onMessageEvent(true);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_NULL_PDU:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Message Sent Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: Message Sent Failed");
                messageInterface.onMessageEvent(false);
                break;
        }
    }
}
