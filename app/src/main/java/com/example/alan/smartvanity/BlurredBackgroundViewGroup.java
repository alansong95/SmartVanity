package com.example.alan.smartvanity;

import android.content.Context;
import android.view.ViewGroup;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by Orlando on 2/27/2018.
 */

public class BlurredBackgroundViewGroup extends ViewGroup {

    Context context;

    public BlurredBackgroundViewGroup(Context context) {
        super(context);
        this.context = context;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
