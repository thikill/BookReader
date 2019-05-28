/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.justwayward.reader.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.justwayward.reader.R;
import com.justwayward.reader.utils.PermissionsChecker;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.justwayward.reader.utils.PermissionsChecker;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.tvSkip)
    TextView tvSkip;

    private boolean flag = false;
    private Runnable runnable;
    private static final int PERMISSIONS_REQUEST_STORAGE = 0;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE

    };
    private PermissionsChecker mPermissionsChecker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        runnable = new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        };
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            mPermissionsChecker = new PermissionsChecker(this);
            requestPermission();
        }
    }


    private void requestPermission(){
        //获取读取和写入SD卡的权限
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)){
            //是否应该展示详细信息
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
/*                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                this.startActivityForResult(intent, PERMISSIONS_REQUEST_STORAGE);*/
            } else {
                //请求权限
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE);

            }
        } else {
                tvSkip.postDelayed(runnable, 2000);

                tvSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goHome();
                    }
                });
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
/*                // 如果取消权限，则返回的值为0
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限成功

                } else {
                    //申请权限失败
                }*/

                //自动跳过
                tvSkip.postDelayed(runnable, 2000);

                tvSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goHome();
                    }
                });
                return;
            }
        }
    }

    private synchronized void goHome() {
        if (!flag) {
            flag = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        tvSkip.removeCallbacks(runnable);
        ButterKnife.unbind(this);
    }
}
