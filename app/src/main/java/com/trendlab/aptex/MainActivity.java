package com.trendlab.aptex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.trendlab.binder_annotation.BindView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv111)
    public TextView tv111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BinderViewTools.bind(this);
        tv111.setText("hello binder");
    }
}