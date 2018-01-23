package com.login.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * description:
 * author: Darren on 2018/1/22 15:08
 * email: 240336124@qq.com
 * version: 1.0
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String key = getIntent().getStringExtra("key");
        Toast.makeText(this, key, Toast.LENGTH_LONG).show();
    }
}
