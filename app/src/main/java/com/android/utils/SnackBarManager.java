package com.android.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

/**
 * Created by SinghParamveer on 11/16/2017.
 */

public final class SnackBarManager {

    private static Snackbar networkSnackbar;

    public static void SnackBar(String message, String action, ActionClickListener actionClickListener, Activity activity) {
        if (networkSnackbar != null && networkSnackbar.isShowing() && message == null)
            networkSnackbar.dismiss();
        if (networkSnackbar != null && networkSnackbar.isShowing())
            networkSnackbar.dismiss();
        if (message == null)
            return;
        networkSnackbar = com.nispok.snackbar.Snackbar.with(activity) // context
                .text(message) // text to be displayed
                .type(SnackbarType.MULTI_LINE)
                .textColor(Color.WHITE) // change the text color
                .textTypeface(Typeface.DEFAULT) // change the text font
                .animation(true);
//                        .color(Color.BLUE) // change the background color

        if (action != null && actionClickListener != null) {
            networkSnackbar.swipeToDismiss(false)
                    .dismissOnActionClicked(false)
                    .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                    .actionLabel(action) // action button label
                    .actionColor(Color.RED) // action button label color
                    .actionLabelTypeface(Typeface.DEFAULT_BOLD) // change the action button font
                    .actionListener(actionClickListener);// action button's ActionClickListener;
        }
        SnackbarManager.show(networkSnackbar, activity); // activity where it is displayed

    }

    public static void showSnackBar(String message, Activity activity) {
        SnackBar(message, null, null, activity);
    }

    public static void showSnackBar(String message, String action, ActionClickListener actionClickListener, Activity activity) {
        SnackBar(message, action, actionClickListener, activity);
    }
}
