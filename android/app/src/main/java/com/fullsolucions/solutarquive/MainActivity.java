package com.fullsolucions.solutarquive;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(NativeExportPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
