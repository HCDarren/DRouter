package com.darren.drouter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.drouter.api.core.DRouter;
import com.drouter.api.result.ActionCallback;
import com.drouter.api.result.RouterResult;

/**
 * description:
 * author: Darren on 2018/1/22 09:43
 * email: 240336124@qq.com
 * version: 1.0
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {

        DRouter.getInstance()
                .action("login/action")
                .context(MainActivity.this)
                .param("key", "value")
                .invokeAction(new ActionCallback() {
                    @Override
                    public void onInterrupt() {

                    }

                    @Override
                    public void onResult(RouterResult result) {
                        Log.e("TAG", "result = " + (result.getObject()));
                    }
                });

    }
}
