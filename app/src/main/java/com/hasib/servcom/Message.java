package com.hasib.servcom;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:38 PM
 */

public class Message {
    private final String number;
    private final String message;

    public Message(String number, String message) {
        this.number = number;
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public String getBody() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "number='" + number + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
