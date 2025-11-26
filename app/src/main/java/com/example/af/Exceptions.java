package com.example.af;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;

public class Exceptions {
    public static String getNameAndMessage(Exception e) {
        return String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage());
    }
    public static OnFailureListener showFailureToast(Context context, String message) {
        return e -> {
            String msg = message + ": " + getNameAndMessage(e);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        };
    }
}
