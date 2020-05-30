package com.example.android.parkingshare.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;

import com.example.android.parkingshare.R;

public class DialogHelper {

    public static AlertDialog.Builder alertBuilder(Context context){
        return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.ShowAlertDialogTheme));
    }
}
