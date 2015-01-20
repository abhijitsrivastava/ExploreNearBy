/*
 * Copyright (c) 2014 COEverywhere. All rights reserved.
 */

package com.coeverywhere.glass.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.coeverywhere.glass.R;

/**
 * Created by ryaneldridge on 5/28/14.
 */
public class InstructionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_instructions);
    }
}
