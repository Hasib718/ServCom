package com.hasib.servcom;

import android.content.Context;

import java.util.Date;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public interface CallReceiver {
    void onIncomingCallReceived(Context ctx,/* String number,*/ Date start);

    void onIncomingCallAnswered(Context ctx,/* String number,*/ Date start);

    void onIncomingCallEnded(Context ctx,/* String number,*/ Date start, Date end);

    void onOutgoingCallStarted(Context ctx,/* String number,*/ Date start);

    void onOutgoingCallEnded(Context ctx,/* String number,*/ Date start, Date end);

    void onMissedCall(Context ctx,/* String number,*/ Date start);
}
