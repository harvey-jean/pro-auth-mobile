package com.jean.productauthentifier.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jean.productauthentifier.R;

import androidx.appcompat.app.AlertDialog;

public class CommonProvider {
    private static final int FLAG_SUCCESS = 0;

    private static final int FLAG_FAILURE = 1;

    private static final int FLAG_INFO = 2;

    public  static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showAlert(String message, Activity context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false)
                .setIcon(R.drawable.red_cross)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlertDialog(Activity activity, String title, String msg, int flag) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialogbox);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textVTitle = (TextView) dialog.findViewById(R.id.title_dialogbox);
        textVTitle.setText(title);
        TextView textVMsg = (TextView) dialog.findViewById(R.id.msg_dialogbox);
        textVMsg.setText(msg);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.image_dialogbox);

        if(flag == FLAG_SUCCESS)
            imageView.setImageResource(R.drawable.mark_tick);

        if(flag == FLAG_FAILURE)
            imageView.setImageResource(R.drawable.red_cross);

        if(flag == FLAG_INFO)
            imageView.setImageResource(R.drawable.info_icon);

        TextView dialogBtn_remove = dialog.findViewById(R.id.txtClose);
        dialogBtn_remove.setOnClickListener(view -> {
            dialog.dismiss();
            //activity.finish();
        });

        dialog.show();
    }

}
