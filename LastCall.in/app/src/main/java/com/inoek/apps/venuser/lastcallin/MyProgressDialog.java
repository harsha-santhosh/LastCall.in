package com.inoek.apps.venuser.lastcallin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Manohar on 28/9/17.
 */

public class MyProgressDialog {

    Dialog dialog;
    private ProgressBar progressBar;
    private TextView textView;

    public MyProgressDialog(Context context) {

        dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);


        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        progressBar = new ProgressBar(context);
        progressBar.setId(R.id.progressBarDialog);
        textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams_progress = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParams_text = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams_progress.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams_text.addRule(RelativeLayout.BELOW,R.id.progressBarDialog);

        LinearLayout.LayoutParams linearlayoutParams_progress = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        linearlayoutParams_progress.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams_progress);
        textView.setLayoutParams(layoutParams_text);

        relativeLayout.addView(progressBar);
        relativeLayout.addView(textView);

        dialog.getWindow().setContentView(relativeLayout, layoutParams);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));


    }

    public void setText(String text)
    {
        textView.setText(text);
    }

    public void show() {

        if (!dialog.isShowing() && dialog != null) {
            dialog.show();

        }

    }

    public void dismiss() {

        if (dialog.isShowing() && dialog != null) {
            dialog.dismiss();
        }
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }


    public void setCanceledOnTouchOutside(boolean flag) {
        dialog.setCanceledOnTouchOutside(flag);
    }

    public void setColor(int colour) {
        progressBar.getIndeterminateDrawable().setColorFilter(colour, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public boolean isShowing() {

        return dialog.isShowing();
    }


}
