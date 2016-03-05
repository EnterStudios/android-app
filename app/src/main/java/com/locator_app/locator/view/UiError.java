package com.locator_app.locator.view;

import android.content.Context;
import android.widget.Toast;

import com.locator_app.locator.apiservice.errorhandling.RequestError;

public class UiError {
    public static boolean showError(Context context, Throwable error) {
        if (error instanceof RequestError &&
                ((RequestError) error).getType() == RequestError.RequestErrorType.ServerUnreachable) {
            Toast.makeText(context, "Kein Internet :(", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public static void showError(Context context, Throwable error, String message) {
        if (!showError(context, error)) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
